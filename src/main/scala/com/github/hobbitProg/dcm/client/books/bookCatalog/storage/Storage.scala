package com.github.hobbitProg.dcm.client.books.bookCatalog.storage

import doobie.imports.Transactor

import scala.collection.Set

import scalaz.concurrent.Task

import com.github.hobbitProg.dcm.client.books.Categories
import com.github.hobbitProg.dcm.client.books.bookCatalog.Book

/**
  * Interface for book catalog storage
  * @author Kyle Cranmer
  * @since 0.1
  */
trait Storage {
  /**
    * Save book into storage
    * @param bookToSave Book to place into storage
    * @return Updated storage when book can be added to be storage
    */
  def save(
    bookToSave: Book
  ): Option[Storage]

  /**
    * Categories that can be associated with books
    * @return Categories that can be associated with books
    */
  def definedCategories: Set[Categories]

  /**
    * Books that exist in storage
    * @return Books that exist in storage
    */
  def contents: Set[Book]
}

object Storage {
  /**
    * Create database storage for book catalog
    * @param catalogTransactor Performs transactions on book catalog database
    * @return Database storage for book catalog
    */
  def apply(
      catalogTransactor: Transactor[Task]
  ): Storage = {
    new DatabaseStorage(
      catalogTransactor
    )
  }
}
