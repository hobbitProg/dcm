package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service

import scala.collection.Set
import scala.util.Either

import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * Repository to use in testing book catagory
  * @author Kyle Cranmer
  * @since 0.1
  */
class FakeRepository
    extends BookRepository {
  // Book that was placed into repository
  var savedBook: Book = _

  /**
    * Save book into repository
    * @param bookToSave Book to place into repository
    * @return A disjoint union of either an error or the book that was added to
    * the repository
    */
  def save(
    bookToSave: Book
  ): Either[String, Book] = {
    savedBook = bookToSave
    Right(bookToSave)
  }

  /**
    * Categories available for books
    */
  def definedCategories: Set[Categories] = Set()

  /**
    * All books that exist within repository
    */
  def contents: Set[Book] = Set()

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
  ): Boolean = {
    title == "Goblins" &&
    author == "Charles Grant"
  }

  /**
    * Determine if book with given ISBN already exists in storage
    * @param isbn ISBN of book that is to be placed into storage
    * @return True if book with given ISBN already exists in storage and false
    * otherwise
    */
  def alreadyContains(
    isbn: ISBNs
  ): Boolean = false
}
