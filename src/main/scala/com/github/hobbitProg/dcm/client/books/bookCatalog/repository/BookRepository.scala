package com.github.hobbitProg.dcm.client.books.bookCatalog.repository

import scala.util.Either

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
}
