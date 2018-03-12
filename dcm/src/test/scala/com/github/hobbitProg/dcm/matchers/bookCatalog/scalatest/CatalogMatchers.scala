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

  class CatalogContainsBookMatcher(val expectedBook: Book) extends Matcher[Try[BookCatalog]] {
    def apply(
      left: Try[BookCatalog]
    ) =
      MatchResult(
        catalogContainsBook(
          left
        ),
        "Book does not exist within catalog",
        "Book exists within catalog"
      )

    private def catalogContainsBook(
      catalog: Try[BookCatalog]
    ) : Boolean =
      catalog match {
        case Success(populatedCatalog) =>
          getByISBN(
            populatedCatalog,
            expectedBook.isbn
          ) match {
            case Success(matchedBook) =>
              matchedBook == expectedBook
            case Failure(_) => false
          }
        case Failure(_) => false
      }
  }

  def containBook(expectedBook: Book) =
    new CatalogContainsBookMatcher(
      expectedBook
    )

  class ServiceGeneratedCatalogContainsBookMatcher(
    val expectedBook: Book
  ) extends Matcher[Validated[BookCatalogError, BookCatalog]] {
    def apply(
      left: Validated[BookCatalogError, BookCatalog]
    ) =
      MatchResult(
        catalogContainsBook(
          left
        ),
        "Book does not exist within catalog",
        "Book exists within catalog"
      )

    private def catalogContainsBook(
      catalog:Validated[BookCatalogError, BookCatalog]
    ) : Boolean =
      catalog match {
        case Valid(resultingCatalog) =>
          getByISBN(
            resultingCatalog,
            expectedBook.isbn
          ) match {
            case Success(matchedBook) =>
              matchedBook == expectedBook
            case Failure(_) => false
          }
        case Invalid(_) =>
          false
      }
  }

  def haveBook(expectedBook : Book) =
    new ServiceGeneratedCatalogContainsBookMatcher(
      expectedBook
    )
}
