package com.github.hobbitProg.dcm.client.books.bookCatalog.service
package interpreter

import scala.collection.Set

import cats.data.Kleisli
import cats.data.Validated.Valid

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookCatalogRepository

/**
  * Interpreter for service handling catalog containing books
  * @author Kyle Cranmer
  * @since 0.2
  */
object BookCatalogServiceInterpreter
    extends BookCatalogService[BookCatalog] {
  /**
    * Add a book to the given book catalog
    * @param catalog Catalog being modified
    * @param title Title of new book
    * @param author Author of new book
    * @param isbn ISBN of new book
    * @param description Description of new book
    * @param cover Cover of new book
    * @param categories Categories of new book
    * @return Routine to add book to catalog and repository
    */
  def addBook(
    catalog: BookCatalog,
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    cover: CoverImages,
    categories: Set[Categories]
  ): BookCatalogOperation[BookCatalog] = Kleisli {
    repository: BookCatalogRepository =>
    Valid(catalog)
  }
}
