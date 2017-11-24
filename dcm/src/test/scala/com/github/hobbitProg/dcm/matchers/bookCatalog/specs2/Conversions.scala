package com.github.hobbitProg.dcm.matchers.bookCatalog.specs2

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
}
