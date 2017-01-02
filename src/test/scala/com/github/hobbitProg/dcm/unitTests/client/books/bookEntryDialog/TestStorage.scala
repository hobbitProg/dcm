package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import com.github.hobbitProg.dcm.client.books.Categories
import com.github.hobbitProg.dcm.client.books.bookCatalog.Book
import com.github.hobbitProg.dcm.client.books.bookCatalog.storage.Storage

import scala.collection.Set

/**
  * Storage to use in book entry dialog tests
  * @author Kyle Cranmer
  * @since 0.1
  */
class TestStorage
  extends Storage {
  /**
    * Save book into storage
    *
    * @param bookToSave Book to place into storage
    */
  override def save(
    bookToSave: Book
  ): Unit = {
  }

  /**
    * Categories that can be associated with books
    *
    * @return Categories that can be associated with books
    */
  override def definedCategories: Set[Categories] = {
    Set[Categories]()
  }

  /**
    * Books that exist in storage
    *
    * @return Books that exist in storage
    */
  override def contents: Set[Book] = {
    Set[Book]()
  }
}
