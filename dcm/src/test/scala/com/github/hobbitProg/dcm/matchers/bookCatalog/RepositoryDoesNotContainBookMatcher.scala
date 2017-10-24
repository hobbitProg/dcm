package com.github.hobbitProg.dcm.matchers.bookCatalog

import org.specs2.matcher.{Expectable, Matcher, MatchResult}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.Book
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookCatalogRepository

/**
  * Matcher that determines if a given book does not exist in a given
  * repository
  * @author Kyle Cranmer
  * @since 0.2
  */
case class RepositoryDoesNotContainBookMatcher(
  val expectedBook: Book
) extends Matcher[BookCatalogRepository] {

  /**
    * Determine if the given book does not exist in the given repository
    * @param catalogResult Expectable containing catalog being examined
    * @return Result of determining if book does not exist in given catalog
    */
  def apply[S <: BookCatalogRepository](
    repository: Expectable[S]
  ): MatchResult[S] =
    result(
      doesNotContainBook(
        repository.value
      ),
      repository.description + " does not contain " + expectedBook.title + " by " + expectedBook.author,
      repository.description + " contains " + expectedBook.title + " by " + expectedBook.author,
      repository
    )

  // Determine if repository does not contain book
  private def doesNotContainBook(
    repository: BookCatalogRepository
  ) =
    (repository retrieve expectedBook.isbn) match {
      case Right(_) =>
        false
      case Left(_) => true
    }
}
