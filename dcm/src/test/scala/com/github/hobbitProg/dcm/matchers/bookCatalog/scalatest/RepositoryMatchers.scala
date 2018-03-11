package com.github.hobbitProg.dcm.matchers.bookCatalog.scalatest

import scala.util.{Left, Right}

import org.scalatest._
import matchers._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookCatalogRepository

/**
  * Matchers for verifying properties about book repositories
  */
trait RepositoryMatchers {

  class RepositoryContainsBookMatcher(
    val expectedBook: Book
  ) extends Matcher[BookCatalogRepository] {
    def apply(
      left: BookCatalogRepository
    ) =
      MatchResult(
        repositoryContainsBook(
          left
        ),
        "Repository does not contain book",
        "Repository contains book"
      )

    private def repositoryContainsBook(
      repository: BookCatalogRepository
    ) : Boolean = {
      import repository._
      retrieve(
        expectedBook.isbn
      ) match {
        case Left(_) =>
          false
        case Right(retrievedBook) =>
          retrievedBook == expectedBook
      }
    }
  }

  def includeBook(
    expectedBook: Book
  ) =
    new RepositoryContainsBookMatcher(
      expectedBook
    )

  class RepositoryDoesNotContainBookMatcher(
    val expectedBook: Book
  ) extends Matcher[BookCatalogRepository] {
    def apply(
      left: BookCatalogRepository
    ) =
      MatchResult(
        repositoryDoesNotContainBook(
          left
        ),
        "Repositorory contains book",
        "Repository does not contain book"
      )

    private def repositoryDoesNotContainBook(
      repository: BookCatalogRepository
    ) : Boolean = {
      import repository._
      retrieve(
        expectedBook.isbn
      ) match {
        case Left(_) =>
          true
        case Right(_) =>
          false
      }
    }
  }

  def notIncludeBook(
    expectedBook: Book
  ) =
    new RepositoryDoesNotContainBookMatcher(
      expectedBook
    )
}
