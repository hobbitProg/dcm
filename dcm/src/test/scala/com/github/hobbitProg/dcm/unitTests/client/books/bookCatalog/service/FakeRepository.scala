package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service

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
    * Add given book to repository
    * @param newBook Book to add to repository
    * @return Disjoint union of either description of error or book that was
    * added to repository
    */
  def add(
    newBook: Book
  ): Either[String, Book] =
    Left(
      "Unimplemented"
    )

  /**
    * Modify given book in repository
    * @param originalBook Book that is being modified
    * @param updatedBook Book that has been updated
    * @return Disjoint union of either description of error or updated book
    */
  def update(
    originalBook: Book,
    updatedBook: Book
  ): Either[String, Book] =
    Left(
      "Unimplemented"
    )

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
  ): Boolean = true

  /**
    * Determine if book with given ISBN already exists in storage
    * @param isbn ISBN of book that is to be placed into storage
    * @return True if book with given ISBN already exists in storage and false
    * otherwise
    */
  def alreadyContains(
    isbn: ISBNs
  ): Boolean = true
}
