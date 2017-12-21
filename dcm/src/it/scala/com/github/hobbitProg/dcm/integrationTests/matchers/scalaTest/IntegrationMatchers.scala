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
    * Determines if a given book was retrieved from the catalog
    */
  class CatalogBookMatcher(
    private val expectedBook: Book
  ) extends Matcher[Try[Book]] {
    def apply(
      left: Try[Book]
    ) =
      MatchResult(
        left == Success(expectedBook),
        "Book exists within the catalog",
        "Book does not exist within the catalog"
      )
  }

  def beInCatalog(expectedBook: Book) =
    new CatalogBookMatcher(
      expectedBook
    )

  /**
    * Determines if a given book was retrieved from the repository
    */
  class RepositoryBookMatcher(
    private val expectedBook: Book
  ) extends Matcher[Either[String, Book]]{
    def apply(
      left: Either[String, Book]
    ) =
      MatchResult(
        left == Right(expectedBook),
        "Book exists within the repository",
        "Book does not exist within the repository"
      )
  }

  def beInRepository(
    expectedBook: Book
  ) =
    new RepositoryBookMatcher(
      expectedBook
    )
}

object IntegrationMatchers extends IntegrationMatchers {
}
