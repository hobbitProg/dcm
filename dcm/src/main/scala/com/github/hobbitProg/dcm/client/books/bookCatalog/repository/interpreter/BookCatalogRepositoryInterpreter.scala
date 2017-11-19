package com.github.hobbitProg.dcm.client.books.bookCatalog.repository.interpreter

import java.net.URI

import cats._
import cats.data._
import cats.implicits._

import doobie.imports._

import fs2.Task
import fs2.interop.cats._

import scala.util.{Either, Left, Right}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookCatalogRepository

class BookCatalogRepositoryInterpreter
    extends BookCatalogRepository {
  private type CategoryMappingType = (ISBNs,Categories)
  private type BookType = (Titles, Authors, ISBNs, String, String)
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
    * Add given book to repository
    * @param newBook Book to add to repository
    * @return Disjoint union of either description of error or book that was
    * added to repository
    */
  override def add(
    newBook: Book
  ): Either[String, Book] = {
    newBook match {
      case noTitleDefined if newBook.title == "" =>
        Left("Given book does not have a title")
      case noAuthorDefined if newBook.author == "" =>
        Left("Given book does hot have an author")
      case noISBNDefined if newBook.isbn == "" =>
        Left("Given book does not have an ISBN")
      case titleAuthorPairAlreadyExists if alreadyContains(
        newBook.title,
        newBook.author
      ) =>
        Left(
          "The book " +
            newBook.title +
            " by " +
            newBook.author +
            " already exists in catalog"
        )
      case isbnAlreadyExists if alreadyContains(
        newBook.isbn
      ) =>
        Left(
          "The book with isbn " +
            newBook.isbn +
            " already exists in catalog"
        )
      case _ =>
        val descriptionToSave =
          newBook.description match {
            case Some(descriptionValue) => descriptionValue
            case None => "NULL"
          }
        val coverImageToSave =
          newBook.coverImage match {
            case Some(bookCover) => bookCover.toString
            case None => "NULL"
          }
        val categoriesToInsert =
          newBook.categories.map {
            category =>
            new CategoryMappingType(
              newBook.isbn,
              category
            )
          }.toList
        val mainBookStatement =
          sql"INSERT INTO bookCatalog(Title,Author,ISBN,Description,Cover)VALUES(${
            newBook.title
          },${
            newBook.author
          },${
            newBook.isbn
          },$descriptionToSave,$coverImageToSave);"
        val statementToInsertCategories =
          Update[CategoryMappingType](
            "INSERT INTO categoryMapping(ISBN,Category)VALUES(?,?);"
          )
        val insertStatement =
          for {
            mainBookInsert <- mainBookStatement.update.run
            categoryUpdate <-
            statementToInsertCategories.updateMany(
              categoriesToInsert
            )
          } yield mainBookInsert + categoryUpdate
        val insertedBook =
          insertStatement.transact(
            databaseConnection
          ).unsafeRunSync
        Right(newBook)
    }
  }

  /**
    * Modify given book in repository
    * @param originalBook Book that is being modified
    * @param updatedBook Book that has been updated
    * @return Disjoint union of either description of error or updated book
    */
  override def update(
    originalBook: Book,
    updatedBook: Book
  ): Either[String, Book] = {
    // Remove original book from repository
    val bookRemovalStatement =
      sql"DELETE FROM bookCatalog WHERE ISBN=${originalBook.isbn};"
    val categoryRemovalStatement =
      sql"DELETE FROM categoryMapping WHERE ISBN=${originalBook.isbn};"
    val removalStatement =
      for {
        mainTableRemoval <- bookRemovalStatement.update.run
        categoryTableRemoval <- categoryRemovalStatement.update.run
      } yield mainTableRemoval + categoryTableRemoval
    val removedBook =
      removalStatement.transact(
        databaseConnection
      ).unsafeRunSync

    // Place updated book into repository
    add(
      updatedBook
    )
  }

  /**
    * Retrieve book with given ISBN
    * @param isbn ISBN of book to retrieve
    * @return Disjoint union of either description of error or book with given
    * ISBN
    */
  override def retrieve(
    isbn: ISBNs
  ): Either[String, Book] =
    Left("Unimplement")

  /**
    * Determine if book with given title and author already exists in storage
    * @param title Title of book that is to be placed into storage
    * @param author Author of book that is to be placed into storage
    * @return True if book with given title and author already exists in
    * storage and false otherwise
    */
  override def alreadyContains(
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
  override def alreadyContains(
    isbn: ISBNs
  ): Boolean = {
    !sql"SELECT ISBN from bookCatalog where ISBN=${isbn};"
      .query[ISBNs]
      .list
      .transact(databaseConnection)
      .unsafeRun
      .isEmpty
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
}

object BookCatalogRepositoryInterpreter
    extends BookCatalogRepositoryInterpreter
