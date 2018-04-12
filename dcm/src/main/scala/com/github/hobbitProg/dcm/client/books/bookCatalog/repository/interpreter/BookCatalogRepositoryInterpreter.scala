package com.github.hobbitProg.dcm.client.books.bookCatalog.repository
package interpreter

import java.net.URI

import cats._
import cats.data._
import Validated._
import cats.implicits._

import doobie._, doobie.implicits._

import cats._, cats.data._, cats.effect._, cats.implicits._

import scala.util.{Try, Success, Failure}

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
  private var databaseConnection: Transactor[IO] = _

  /**
    * Save connection to database containing book catalog
    * @param connection Connection to database containing book catalog
    */
  def setConnection(
    connection: Transactor[IO]
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
  ): Try[Book] = {
    newBook match {
      case noTitleDefined if newBook.title == "" =>
        Failure(
          new NoTitleException()
        )
      case noAuthorDefined if newBook.author == "" =>
        Failure(
          new NoAuthorException()
        )
      case noISBNDefined if newBook.isbn == "" =>
        Failure(
          new NoISBNException()
        )
      case titleAuthorPairAlreadyExists if alreadyContains(
        newBook.title,
        newBook.author
      ) =>
        Failure(
          new DuplicateTitleAndAuthorException(
            newBook.title,
            newBook.author
          )
        )
      case isbnAlreadyExists if alreadyContains(
        newBook.isbn
      ) =>
        Failure(
          new DuplicateISBNException(
            newBook.isbn
          )
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
        Success(newBook)
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
  ): Try[Book] = {
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
  ): Either[String, Book] = {
    val bookInfo: Set[BookType] =
      sql"SELECT Title,Author,ISBN,Description,Cover FROM bookCatalog WHERE ISBN=${isbn};"
        .query[BookType]
        .to[Set]
        .transact(databaseConnection)
        .unsafeRunSync

    if (bookInfo.size == 0) {
      Left(
        "No books exist with the ISBN $isbn"
      )
    }
    else {
      val retrievedBookInfo: BookType =
        bookInfo.head
      Book.book(
        retrievedBookInfo._1,
        retrievedBookInfo._2,
        retrievedBookInfo._3,
        retrievedBookInfo._4 match {
          case "NULL" => None
          case description => Some(description)
        },
        retrievedBookInfo._5 match {
          case "NULL" => None
          case location => Some(new URI(location))
        },
        sql"SELECT Category FROM categoryMapping WHERE ISBN=${isbn};"
          .query[Categories]
          .to[Set]
          .transact(databaseConnection)
          .unsafeRunSync
      ) match {
        case Invalid(errorDescription) => Left(errorDescription)
        case Valid(retrievedBook) => Right(retrievedBook)
      }
    }
  }

  /**
    * Retrieve book with given title and author
    * @param title The title of the book to retrieve
    * @param author The author of the book to retrieve
    * @return Disjoint union of either description of erro ro book with given
    * title and author
    */
  override def retrieve(
    title: Titles,
    author: Authors
  ): Either[String, Book] = {
    val bookInfo: Set[BookType] =
      sql"SELECT Title,Author,ISBN,Description,Cover FROM bookCatalog WHERE Title=${title} AND Author=${author};"
        .query[BookType]
        .to[Set]
        .transact(databaseConnection)
        .unsafeRunSync

    if (bookInfo.size == 0) {
      Left(
        "No books exist with the title $title and author $author"
      )
    }
    else {
      val retrievedBookInfo: BookType =
        bookInfo.head
      Book.book(
        retrievedBookInfo._1,
        retrievedBookInfo._2,
        retrievedBookInfo._3,
        retrievedBookInfo._4 match {
          case "NULL" => None
          case description => Some(description)
        },
        retrievedBookInfo._5 match {
          case "NULL" => None
          case location => Some(new URI(location))
        },
        sql"SELECT Category from categoryMapping WHERE ISBN=${retrievedBookInfo._3};"
          .query[Categories]
          .to[Set]
          .transact(databaseConnection)
          .unsafeRunSync
      ) match {
        case Invalid(errorDescription) => Left(errorDescription)
        case Valid(retrievedBook) => Right(retrievedBook)
      }
    }
  }

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
      .unsafeRunSync
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
      .unsafeRunSync
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
         .unsafeRunSync


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
          .unsafeRunSync
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
    * The categories a book can be categorized as
    */
  override def definedCategories: Set[Categories] =
    sql"SELECT Category FROM definedCategories;"
      .query[Categories]
      .to[Set]
      .transact(databaseConnection)
      .unsafeRunSync
}

object BookCatalogRepositoryInterpreter
    extends BookCatalogRepositoryInterpreter {
}
