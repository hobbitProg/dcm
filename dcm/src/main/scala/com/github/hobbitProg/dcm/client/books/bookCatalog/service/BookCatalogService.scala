package com.github.hobbitProg.dcm.client.books.bookCatalog.service

import scala.collection.Set

import cats.data.{Kleisli, Validated}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookCatalogRepository

/**
  * Service interface for a book catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
trait BookCatalogService[Catalog] {
  type BookCatalogValidation[ResultType] = Validated[BookCatalogError, ResultType]
  type BookCatalogOperation[ResultType] = Kleisli[BookCatalogValidation, BookCatalogRepository, ResultType]

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
    catalog: Catalog,
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    cover: CoverImages,
    categories: Set[Categories]
  ): BookCatalogOperation[Catalog]
}
