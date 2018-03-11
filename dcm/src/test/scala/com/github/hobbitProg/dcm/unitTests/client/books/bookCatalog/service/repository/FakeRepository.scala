package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.repository

import scala.util.{Either, Left}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookCatalogRepository

/**
  * Repository for testing book catalog service
  * @author Kyle Cranmer
  * @since 0.2
  */
class FakeRepository
    extends BookCatalogRepository {
  /**
    * Book that was placed into repository
    */
  private var bookPlacedIntoRepository: Book = _

  /**
    * Book that was removed from repository
    */
  var bookRemovedFromRepository: Book = _

  /**
    * Title of book already in repository
    */
  var existingTitle: Titles = _

  /**
    * Author of book already in repository
    */
  var existingAuthor: Authors = _

  /**
    * ISBN of book already in repository
    */
  var existingISBN: ISBNs = _

  /**
    * Add given book to repository
    * @param newBook Book to add to repository
    * @return Disjoint union of either description of error or book that was
    * added to repository
    */
  override def add(
    newBook: Book
  ): Either[String, Book] = {
    bookPlacedIntoRepository = newBook
    Right(
      newBook
    )
  }

  /**
    * Modify given book in repository
    * @param originalBook Book that is being modified
    * @param updatedBook Book that has been updated
    * @return Disjoint union of either description of error or updated book
    */
  override def update(
    originalBook: Book,
    updatedBook: Book
  ): Either[String, Book] = {
    bookPlacedIntoRepository = updatedBook
    bookRemovedFromRepository = originalBook
    Right(
      updatedBook
    )
  }

  /**
    * Retrieve book with given ISBN
    * @param isbn ISBN of book to retrieve
    * @return Disjoint union of either description of error or book with given
    * ISBN
    */
  override def retrieve(
    isbn: ISBNs
  ): Either[String, Book] =
    if (bookPlacedIntoRepository == null) {
      Left("No book was placed into the repository")
    }
    else {
      if (bookPlacedIntoRepository.isbn == isbn) {
        Right(bookPlacedIntoRepository)
      }
      else {
        Left("No book with the ISBN " + isbn + " exists in the repoistory")
      }
    }

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
  ): Either[String, Book] =
    Left("Not implemented")

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
  ): Boolean =
    title == existingTitle &&
  author == existingAuthor

  /**
    * Determine if book with given ISBN already exists in storage
    * @param isbn ISBN of book that is to be placed into storage
    * @return True if book with given ISBN already exists in storage and false
    * otherwise
    */
  override def alreadyContains(
    isbn: ISBNs
  ): Boolean =
    isbn == existingISBN

  /**
    * All books that exist within repository
    */
  override def contents: Set[Book] =
    Set[Book]()

  /**
    * The categories a book can be categorized as
    */
  override def definedCategories: Set[Categories] =
    Set[Categories]()
}
