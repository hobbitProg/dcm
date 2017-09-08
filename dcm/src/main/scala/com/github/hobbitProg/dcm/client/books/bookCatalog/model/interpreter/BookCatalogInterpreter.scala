package com.github.hobbitProg.dcm.client.books.bookCatalog.model.interpreter

import scala.collection.{Seq, Set}
import scala.util.{Try, Success, Failure}

import cats.data.Validated.{Valid, Invalid}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * Interpreter for catalog containing books
  * @author Kyle Cranmer
  * since 0.1
  */
class BookCatalogInterpreter(
  private val catalog: Set[Book] =
    Set[Book](),
  private val subscribers: Seq[Book => Unit] =
    Seq[Book => Unit]()
) extends BookCatalog {

  /**
    * Place new book into catalog
    * @param title Title of new book
    * @param author Author of new book
    * @param isbn ISBN of new book
    * @param description Description of new book
    * @param cover Cover image of new book
    * @param categories Categories of new book
    * @return Function that places book into catalog
    */
  def add(
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    cover: CoverImages,
    categories: Set[Categories]
  ): Try[BookCatalog] = {
    Book.book(
      title,
      author,
      isbn,
      description,
      cover,
      categories
    ) match {
      case Valid(newBook) =>
        for (action <- subscribers) {
          action(
            newBook
          )
        }
        Success(
          new BookCatalogInterpreter(
            catalog + newBook,
            subscribers
          )
        )
      case Invalid(errorReason) =>
        Failure(
          new InvalidBookException(
            errorReason
          )
        )
    }
  }

  /**
    * Register action to perform when book is added to catalog
    * @param addAction Action to perform
    * @param Book catalog with action within add subscribers
    */
  def onAdd(
    addAction: Book => Unit
  ): BookCatalog = {
    new BookCatalogInterpreter(
      catalog,
      subscribers :+ addAction
    )
  }

  /**
    * Attempt to retrieve book from catalog by ISBN
    * @param isbn ISBN of book to retrieve
    * @return Either success with book from catalog if book with given ISBN
    *         exists within catalog and Failure otherwise
    */
  def getByISBN(
    desiredISBN: ISBNs
  ): Try[Book] = {
    catalog.find(
      currentBook =>
      currentBook.isbn == desiredISBN
    ) match {
      case Some(correspondingBook) =>
        Success(
          correspondingBook
        )
      case None =>
        Failure(
          new NoBookHasGivenISBN(
            desiredISBN
          )
        )
    }
  }
}
