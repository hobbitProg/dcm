package com.github.hobbitProg.dcm.client.books.bookCatalog.repository.interpreter

import cats._
import cats.data._
import cats.implicits._

import doobie.imports._

import fs2.Task
import fs2.interop.cats._

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
    * Determine if book with given title and author already exists in storage
    * @param title Title of book that is to be placed into storage
    * @param author Author of book that is to be placed into storage
    * @return True if book with given title and author already exists in
    * storage and false otherwise
    */
  private def alreadyContains(
    title: Titles,
    author: Authors
  ): Boolean = {
    !sql"SELECT Title FROM bookCatalog WHERE Title=${title} AND Author=${author};"
      .query[String]
      .list
      .transact(databaseConnection)
      .unsafeRun
      .isEmpty
  }
}
