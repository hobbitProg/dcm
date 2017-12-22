package com.github.hobbitProg.dcm.integrationTests.matchers.scalaTest

import scala.util.{Try, Success, Either, Right}

import org.scalatest.matchers.{Matcher, MatchResult}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * Matchers for integration tests
  * @author Kyle Cranmer
  * @since 0.2
  */
trait IntegrationMatchers {
  /**
    * The matcher that determines if a given book was retrieved from the catalog
    *
    * @param expectedBook The book that should have been retrieved from the
    * catalog
    */
  class CatalogBookMatcher(
    private val expectedBook: Book
  ) extends Matcher[Try[Book]] {

    /**
      * Determine if a given book was retrieved from the catalog
      *
      * @param left The result from the book catalog
      *
      *  @return The result indicating if the given book was retrieved from the
      *  book
      */
    def apply(
      left: Try[Book]
    ) =
      MatchResult(
        left == Success(expectedBook),
        "Book exists within the catalog",
        "Book does not exist within the catalog"
      )
  }

  /**
    * Create a matcher that determines if a given book was retrieved from the
    * catalog
    *
    * @param expectedBook The book should have been received from the catalog
    *
    * @return A matcher that determines if a given gook was retrieved from the
    * catalog
    */
  def beInCatalog(
    expectedBook: Book
  ) =
    new CatalogBookMatcher(
      expectedBook
    )

  /**
    *  The matcher that determines if a given book was retrieved from the
    *  repository
    *
    * @param expectedBook The book that should have been retrieved from the
    * repository
    */
  class RepositoryBookMatcher(
    private val expectedBook: Book
  ) extends Matcher[Either[String, Book]]{
    /**
      * Determine if a given book was retrieved from the repository
      *
      * @param left The result from the repository
      *
      * @return The result that indicates if the given book was retrieved from
      * the repository
      */
    def apply(
      left: Either[String, Book]
    ) =
      MatchResult(
        left == Right(expectedBook),
        "Book exists within the repository",
        "Book does not exist within the repository"
      )
  }

  /**
    * Create a matcher that determines if a given book was retrieved from the
    * repository
    *
    * @param expectedBook The book that should have been retrieved from the
    * repository
    *
    * @return The matcher that determines if the given book was retrieved from
    * the repository
    */
  def beInRepository(
    expectedBook: Book
  ) =
    new RepositoryBookMatcher(
      expectedBook
    )
}

object IntegrationMatchers extends IntegrationMatchers {
}
