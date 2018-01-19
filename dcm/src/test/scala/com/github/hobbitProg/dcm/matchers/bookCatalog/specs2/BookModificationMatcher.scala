package com.github.hobbitProg.dcm.matchers.bookCatalog.specs2

import scala.collection.Set
import scala.util.{Try, Success, Failure}

import org.specs2.matcher.{Expectable, Matcher, MatchResult}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Matcher to verify a given book is modified
  * @author Kyle Cranmer
  * @since 0.2
  */
case class BookModificationMatcher(
  private val expectedTitle: Titles,
  private val expectedAuthor: Authors,
  private val expectedISBN: ISBNs,
  private val expectedDescription: Description,
  private val expectedCover: CoverImages,
  private val expectedCategories: Set[Categories]
) extends Matcher[Try[BookCatalog]]{
  /**
    * Determine if the modified book exists in the given catalog
    * @param catalogResult Expectable containing catalog being examined
    * @return Result of determining if book exists in given catalog
    */
  def apply[S <: Try[BookCatalog]](
    catalogResult: Expectable[S]
  ) =
    result(
      containsBook(
        catalogResult.value
      ),
      catalogResult.description + " contains " + expectedTitle + " by " + expectedAuthor,
      catalogResult.description + " does not contain " + expectedTitle + " by " + expectedAuthor,
      catalogResult
    )

  // Verify the modified book is in the catalog
  private def containsBook(
    actualCatalog: Try[BookCatalog]
  ): Boolean = {
    actualCatalog.isInstanceOf[Success[BookCatalog]] &&
    bookDataMatches(
      actualCatalog,
      expectedISBN,
      retrievedBook =>
      retrievedBook.title == expectedTitle
    ) &&
    bookDataMatches(
      actualCatalog,
      expectedISBN,
      retrievedBook =>
      retrievedBook.author == expectedAuthor
    ) &&
    bookDataMatches(
      actualCatalog,
      expectedISBN,
      retrievedBook =>
      retrievedBook.isbn == expectedISBN
    ) &&
    bookDataMatches(
      actualCatalog,
      expectedISBN,
      retrievedBook =>
      retrievedBook.description == expectedDescription
    ) &&
    bookDataMatches(
      actualCatalog,
      expectedISBN,
      retrievedBook =>
      retrievedBook.coverImage == expectedCover
    ) &&
    bookDataMatches(
      actualCatalog,
      expectedISBN,
      retrievedBook =>
      retrievedBook.categories == expectedCategories
    )
  }

  // Verify the book datum is as expected
  def bookDataMatches(
    catalogResult: Try[BookCatalog],
    expectedISBN: ISBNs,
    bookPredicate: Book => Boolean
  ): Boolean = {
    catalogResult match {
      case Success(resultingCatalog) =>
        getByISBN(
          resultingCatalog,
          expectedISBN
        ) match {
          case Success(retrievedBook) =>
            bookPredicate(
              retrievedBook
            )
          case Failure(_) =>
            false
        }
      case Failure(_) =>
        false
    }
  }
}
