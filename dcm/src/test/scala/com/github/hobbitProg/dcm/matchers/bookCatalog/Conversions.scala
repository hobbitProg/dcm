package com.github.hobbitProg.dcm.matchers.bookCatalog

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
}
