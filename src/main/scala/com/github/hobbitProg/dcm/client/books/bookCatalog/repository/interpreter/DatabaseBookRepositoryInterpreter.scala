package com.github.hobbitProg.dcm.client.books.bookCatalog.repository.interpreter

import cats._
import cats.data._
import cats.implicits._

import doobie.imports._

import fs2.Task
import fs2.interop.cats._

import java.net.URI

import scala.util.{Either, Left, Right}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository

/**
  * Database implementation of book repository
  * @author Kyle Cranmer
  * @since 0.1
  */
object DatabaseBookRepositoryInterpreter extends BookRepository {
  private type BookType = (Titles, Authors, ISBNs, String, String)
  private type CategoryMappingType = (ISBNs,Categories)
  private class ErrorBookClass(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book {
  }

  // Connection to database containing book catalog
  private var databaseConnection: Transactor[Task] = _

  /**
    * Save connection to database containing book catalog
    * @param connection Connection to database containing book catalog
    */
  def setConnection(
    connection: Transactor[Task]
  ) = {
    databaseConnection = connection
  }

  /**
    * Save book into repository
    * @param bookToSave Book to place into repository
    * @return A disjoint union of either an error or the book that was added to
    * the repository
    */
  override def save(
    bookToSave: Book
  ): Either[String, Book] = {
    bookToSave match {
      case noTitleDefined if bookToSave.title == "" =>
        Left("Given book does not have a title")
      case noAuthorDefined if bookToSave.author == "" =>
        Left("Given book does hot have an author")
      case noISBNDefined if bookToSave.isbn == "" =>
        Left("Given book does not have an ISBN")
      case titleAuthorPairAlreadyExists if alreadyContains(
        bookToSave.title,
        bookToSave.author
      ) =>
        Left(
          "The book " +
            bookToSave.title +
            " by " +
            bookToSave.author +
            " already exist in catalog"
        )
      case _ =>
        val descriptionSQL =
          bookToSave.description match {
            case Some (descriptionValue) => descriptionValue
            case None => "NULL"
          }
        val coverImageSQL =
          bookToSave.coverImage match {
            case Some (bookCover) => bookCover.toString
            case None => "NULL"
          }
        val dataToInsert: String =
          "'${bookToSave.title}','${bookToSave.author}','${bookToSave.isbn}','$descriptionSQL','$coverImageSQL'"
        val categoriesToInsert =
          bookToSave.categories.map (
            category =>
              new CategoryMappingType (
                bookToSave.isbn,
                category
              )
          ).toList
        val mainBookStatement =
          sql"INSERT INTO bookCatalog(Title,Author,ISBN,Description,Cover)VALUES(${
            bookToSave.title
          },${
            bookToSave.author
          },${
            bookToSave.isbn
          },$descriptionSQL,$coverImageSQL);"
        val statementToInsertCategories =
          Update[CategoryMappingType] (
            "INSERT INTO categoryMapping(ISBN,Category)VALUES(?,?);"
          )
        val insertStatement =
          for {
            mainBookInsert <- mainBookStatement.update.run
            categoryUpdate <-
            statementToInsertCategories.updateMany (
              categoriesToInsert
            )
          } yield mainBookInsert + categoryUpdate
        val insertedBook =
          insertStatement.transact (
            databaseConnection
          ).unsafeRunSync

        Right(bookToSave)
    }
  }

  /**
    * Categories available for books
    */
  override def definedCategories: Set[Categories] = {
    sql"SELECT Category FROM definedCategories;"
      .query[Categories]
      .to[Set]
      .transact(databaseConnection)
      .unsafeRun
  }

  /**
    * All books that exist within repository
    */
  override def contents: Set[Book] = {
    // Get main information on defined books
     val bookInfo: Set[BookType] =
       sql"SELECT Title,Author,ISBN,Description,Cover FROM bookCatalog;"
         .query[BookType]
         .to[Set]
         .transact(databaseConnection)
         .unsafeRun


    // Generate books within repository
    bookInfo map {
      bookDatum =>
      Book.book(
        bookDatum._1,
        bookDatum._2,
        bookDatum._3,
        bookDatum._4 match {
          case "NULL" => None
          case description => Some(description)
        },
        bookDatum._5 match {
          case "NULL" => None
          case location => Some(new URI(location))
        },
        sql"SELECT Category FROM categoryMapping WHERE ISBN=${bookDatum._3};"
          .query[Categories]
          .to[Set]
          .transact(databaseConnection)
          .unsafeRun
      ).getOrElse(
        new ErrorBookClass(
          "",
          "",
          "",
          None,
          None,
          Set()
        )
      )
    }
  }

  /**
    * Determine if book with given title and author already exists in storage
    * @param title Title of book that is to be placed into storage
    * @param author Author of book that is to be placed into storage
    * @return True if book with given title and author already exists in
    * storage and false otherwise
    */
  def alreadyContains(
    title: Titles,
    author: Authors
  ): Boolean = {
    !sql"SELECT Title FROM bookCatalog WHERE Title=${title} AND Author=${author};"
      .query[Titles]
      .list
      .transact(databaseConnection)
      .unsafeRun
      .isEmpty
  }

  /**
    * Determine if book with given ISBN already exists in storage
    * @param isbn ISBN of book that is to be placed into storage
    * @return True if book with given ISBN already exists in storage and false
    * otherwise
    */
  def alreadyContains(
    isbn: ISBNs
  ): Boolean = {
    !sql"SELECT ISBN from bookCatalog where ISBN=${isbn};"
      .query[Titles]
      .list
      .transact(databaseConnection)
      .unsafeRun
      .isEmpty
  }
}
