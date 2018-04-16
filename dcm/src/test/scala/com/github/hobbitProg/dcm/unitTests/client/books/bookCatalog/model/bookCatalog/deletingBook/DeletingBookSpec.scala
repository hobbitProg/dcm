package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.deletingBook

import com.github.hobbitProg.dcm.generator.BookGenerators

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Common functionality for specifications of deleting books from the catalog
  * @author Kyle Cranmer
  * @since 0.3
  */
trait DeletingBookSpec
    extends BookGenerators {
  protected case class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book {
  }

  protected type CatalogInfoType = (
    BookCatalog,
    Titles,
    Authors
  )

  protected var givenDeletedBook: Book = null
}
