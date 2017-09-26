package com.github.hobbitProg.dcm.client.books.bookCatalog.repository

import scala.util.Either

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.Book

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
}
