package com.github.hobbitProg.dcm.matchers.bookCatalog.specs2

import scala.collection.Set

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * Conversions to create book catalog matchers
  * @author Kyle Cranmer
  * @since 0.2
  */
object Conversions {
  /**
    * Convert given book to matcher that determines if book exists within
    * catalog
    * @param expectedBook Book expected to be in catalog
    * @return Matcher to see if book exists in catalog
    */
  def containBook(
    expectedBook: Book
  ): CatalogContainsBookMatcher =
    CatalogContainsBookMatcher(
      expectedBook
    )

  /**
    * Convert given book to matcher that determines if book exists within
    * repository
    * @param expectedBook Book expected to be in repository
    * @return Matcher to see if book exists in repository
    */
  def haveBook(
    expectedBook: Book
  ): RepositoryContainsBookMatcher =
    RepositoryContainsBookMatcher(
      expectedBook
    )

  /**
    * Convert given book to matcher that determines if book does not exist
    * within repository
    * @param expectedBook Book expected not to be in repository
    * @return Matcher to see if book does not exist in repository
    */
  def notHaveBook(
    expectedBook: Book
  ): RepositoryDoesNotContainBookMatcher =
    RepositoryDoesNotContainBookMatcher(
      expectedBook
    )

  /**
    * Covert given information to a matcher that determines if a modified book
    * was added to the catalog
    * @param expectedTitle Title of modified book that should be in the catalog
    * @param expectedAuthor Author of modified book that should be in the
    * catalog
    * @param expectedISBN ISBN of modified book that should be in the catalog
    * @param expectedDescription Description of book that should be in the
    * catalog
    * @param expectedCategories Categories of modified book that should be in
    * the catalog
    * @return Matcher to see if the modified book is in the catalog
    */
  def containModifiedBook(
    expectedTitle: Titles,
    expectedAuthor: Authors,
    expectedISBN: ISBNs,
    expectedDescription: Description,
    expectedCover: CoverImages,
    expectedCategories: Set[Categories]
  ) =
    BookModificationMatcher(
      expectedTitle,
      expectedAuthor,
      expectedISBN,
      expectedDescription,
      expectedCover,
      expectedCategories
    )
}
