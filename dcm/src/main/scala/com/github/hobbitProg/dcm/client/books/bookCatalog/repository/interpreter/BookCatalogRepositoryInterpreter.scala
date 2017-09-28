package com.github.hobbitProg.dcm.client.books.bookCatalog.repository.interpreter

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
}

object BookCatalogRepositoryInterpreter
    extends BookCatalogRepositoryInterpreter
