package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import com.github.hobbitProg.dcm.client.books._
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
  ): Option[Storage] = {
    Some(this)
  }

  /**
    * Determine if book with given title and author already exists in storage
    * @param title Title of book that is to be placed into storage
    * @param author Author of book that is to be placed into storage
    * @return True if book with given title and author already exists in
    * storage and false otherwise
    */
  override def alreadyContains(
    title: Titles,
    author: Authors
  ): Boolean = {
    title == "Ruins" &&
    author == "Kevin J. Anderson"
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
