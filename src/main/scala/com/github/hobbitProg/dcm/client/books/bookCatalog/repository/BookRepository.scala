package com.github.hobbitProg.dcm.client.books.bookCatalog.repository

import scala.util.Either

import com.github.hobbitProg.dcm.client.books._
import com.github.hobbitProg.dcm.client.books.bookCatalog.model.Book

/**
  * Algebra for book catalog repostiory
  * @author Kyle Cranmer
  * @since 0.1
  */
trait BookRepository {
  /**
    * Save book into repository
    * @param bookToSave Book to place into repository
    * @return A disjoint union of either an error or the book that was added to
    * the repository
    */
  def save(
    bookToSave: Book
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
}
