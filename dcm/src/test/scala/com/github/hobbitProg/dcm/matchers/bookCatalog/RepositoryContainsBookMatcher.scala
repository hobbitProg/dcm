package com.github.hobbitProg.dcm.matchers.bookCatalog

import scala.util.{Left, Right}

import org.specs2.matcher.{Expectable, Matcher, MatchResult}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookCatalogRepository

case class RepositoryContainsBookMatcher(
  val expectedBook: Book
) extends Matcher[BookCatalogRepository] {
  /**
    * Determine if the given book exists in the given repository
    * @param catalogResult Expectable containing catalog being examined
    * @return Result of determining if book exists in given catalog
    */
  def apply[S <: BookCatalogRepository](
    repository: Expectable[S]
  ): MatchResult[S] =
    result(
      containsBook(
        repository.value
      ),
      repository.description + " contains " + expectedBook.title + " by " + expectedBook.author,
      repository.description + " does not contain " + expectedBook.title + " by " + expectedBook.author,
      repository
    )

  // Determine if repository contains book
  private def containsBook(
    repository: BookCatalogRepository
  ) =
    (repository retrieve expectedBook.isbn) match {
      case Right(retrievedBook) =>
        retrievedBook == expectedBook
      case Left(_) => false
    }
}
