package com.github.dcm.unitTests.client.books.bookCatalog.repository.deletingBook

import scala.collection.Set

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.
  database.StubDatabase

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository

/**
  * Common functionality for specifications of deleting books from the
  * repository
  * @author Kyle Cranmer
  * @since 0.3
  */
trait DeletingBookSpec {
  protected case class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book {
  }

  protected type RepositoryInfoType = (
    StubDatabase,
    BookCatalogRepository,
    ISBNs
  )
}
