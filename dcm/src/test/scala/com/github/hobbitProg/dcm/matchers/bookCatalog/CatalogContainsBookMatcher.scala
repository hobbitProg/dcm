package com.github.hobbitProg.dcm.matchers.bookCatalog

import cats.data.Validated
import Validated._

import scala.util.{Success, Failure}

import org.specs2.matcher.{Expectable, Matcher, MatchResult}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.BookCatalogError

/**
  * Matcher to verify given book exists in given book catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
case class CatalogContainsBookMatcher(
  val expectedBook: Book
) extends Matcher[Validated[BookCatalogError, BookCatalog]] {
  /**
    * Determine if the given book exists in the given catalog
    * @param catalogResult Expectable containing catalog being examined
    * @return Result of determining if book exists in given catalog
    */
  def apply[S <: Validated[BookCatalogError, BookCatalog]](
    catalogResult: Expectable[S]
  ): MatchResult[S] = {
    result(
      containsBook(
        catalogResult.value
      ),
      catalogResult.description + " contains " + expectedBook.title + " by " + expectedBook.author,
      catalogResult.description + " does not contain " + expectedBook.title + " by " + expectedBook.author,
      catalogResult
    )
  }

  // Determine if book catalog contains book
  private def containsBook(
    catalogResult: Validated[BookCatalogError, BookCatalog]
  ): Boolean = {
    catalogResult match {
      case Valid(catalog) =>
        getByISBN(catalog, expectedBook.isbn) match {
          case Success(matchingBook) =>
            matchingBook == expectedBook
          case Failure(_) => false
        }
      case Invalid(_) => false
    }
  }
}
