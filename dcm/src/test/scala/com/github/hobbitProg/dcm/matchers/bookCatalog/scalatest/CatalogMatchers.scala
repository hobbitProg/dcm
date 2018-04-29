package com.github.hobbitProg.dcm.matchers.bookCatalog.scalatest

import cats.data.Validated
import Validated.{Valid, Invalid}

import scala.util.{Try, Success, Failure}

import org.scalatest._
import matchers._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.BookCatalogError

/**
  * The matchers for verifying properties about book catalogs
  * @author Kyle Cranmer
  * @since 0.2
  */
trait CatalogMatchers {

  class CatalogContainsBookMatcher(val expectedBook: Book) extends Matcher[BookCatalog] {
    def apply(
      left: BookCatalog
    ) =
      MatchResult(
        catalogContainsBook(
          left
        ),
        "Book does not exist within catalog",
        "Book exists within catalog"
      )

    private def catalogContainsBook(
      catalog: BookCatalog
    ) : Boolean =
      getByISBN(
        catalog,
        expectedBook.isbn
      ) match {
        case Success(matchedBook) =>
          matchedBook == expectedBook
        case Failure(_) => false
      }
  }

  def containBook(expectedBook: Book) =
    new CatalogContainsBookMatcher(
      expectedBook
    )

  class ServiceGeneratedCatalogContainsBookMatcher(
    val expectedBook: Book
  ) extends Matcher[BookCatalog]{
    def apply(
      left:  BookCatalog
    ) =
      MatchResult(
        catalogContainsBook(
          left
        ),
        "Book does not exist within catalog",
        "Book exists within catalog"
      )

    private def catalogContainsBook(
      catalog: BookCatalog
    ) : Boolean =
      getByISBN(
        catalog,
        expectedBook.isbn
      ) match {
        case Success(matchedBook) =>
          matchedBook == expectedBook
        case Failure(_) => false
      }
  }

  def haveBook(expectedBook : Book) =
    new ServiceGeneratedCatalogContainsBookMatcher(
      expectedBook
    )
}
