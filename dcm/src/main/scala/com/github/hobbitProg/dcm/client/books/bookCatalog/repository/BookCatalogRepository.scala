package com.github.hobbitProg.dcm.client.books.bookCatalog.repository

import scala.collection.Set
import scala.util.Either

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.{Book, Titles,
  Authors, ISBNs, Categories}

/**
  * Interface for repository for book catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
trait BookCatalogRepository {
  /**
    * Add given book to repository
    * @param newBook Book to add to repository
    * @return Disjoint union of either description of error or book that was
    * added to repository
    */
  def add(
    newBook: Book
  ): Either[String, Book]

  /**
    * Modify given book in repository
    * @param originalBook Book that is being modified
    * @param updatedBook Book that has been updated
    * @return Disjoint union of either description of error or updated book
    */
  def update(
    originalBook: Book,
    updatedBook: Book
  ): Either[String, Book]

  /**
    * Retrieve book with given ISBN
    * @param isbn ISBN of book to retrieve
    * @return Disjoint union of either description of error or book with given
    * ISBN
    */
  def retrieve(
    isbn: ISBNs
  ): Either[String, Book]

  /**
    * Retrieve book with given title and author
    * @param title The title of the book to retrieve
    * @param author The author of the book to retrieve
    * @return Disjoint union of either description of erro ro book with given
    * title and author
    */
  def retrieve(
    title: Titles,
    author: Authors
  ): Either[String, Book]

  /**
    * Determine if book with given title and author already exists in storage
    * @param title Title of book that is to be placed into storage
    * @param author Author of book that is to be placed into storage
    * @return True if book with given title and author already exists in
    * storage and false otherwise
    */
  def alreadyContains(
    title: Titles,
    author: Authors
  ): Boolean

  /**
    * Determine if book with given ISBN already exists in storage
    * @param isbn ISBN of book that is to be placed into storage
    * @return True if book with given ISBN already exists in storage and false
    * otherwise
    */
  def alreadyContains(
    isbn: ISBNs
  ): Boolean

  /**
    * All books that exist within repository
    */
  def contents: Set[Book]

  /**
    * The categories a book can be categorized as
    */
  def definedCategories: Set[Categories]
}
