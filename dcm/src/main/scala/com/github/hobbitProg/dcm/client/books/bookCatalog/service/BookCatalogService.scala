package com.github.hobbitProg.dcm.client.books.bookCatalog.service

import scala.collection.Set

import cats.Id
import cats.data.{Kleisli, Validated}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository

/**
  * Service interface for a book catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
trait BookCatalogService[Catalog] {
  type BookCatalogValidation[ResultType] =
    Validated[BookCatalogError, ResultType]
  type BookCatalogOperation[ResultType] =
    Kleisli[BookCatalogValidation, BookCatalogRepository, ResultType]
  type BookCatalogQuery[ResultType] =
    Kleisli[Id, BookCatalogRepository, ResultType]

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
  def insertBook(
    catalog: Catalog,
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    cover: CoverImages,
    categories: Set[Categories]
  ): BookCatalogOperation[(Catalog, BookCatalogRepository)]

  /**
    * Modify a book within the given book catalog
    * @param catalog Catalog being modified
    * @param originalBook Book that already exists in catalog
    * @param newTitle New title of book
    * @param newAuthor New author of book
    * @param newISBN New ISBN of book
    * @param newDescription New description of book
    * @param newCover New cover of book
    * @param newCategories New categories of book
    * @return Routine to modify book in catalog and repository
    */
  def modifyBook(
    catalog: Catalog,
    originalBook: Book,
    newTitle: Titles,
    newAuthor: Authors,
    newISBN: ISBNs,
    newDescription: Description,
    newCover: CoverImages,
    newCategories: Set[Categories]
  ): BookCatalogOperation[Catalog]

  /**
    * Determine if book with given title and author exists within catalog
    * @param catalog Catalog being queried
    * @param title Title of book being examined
    * @param author Author of book being examined
    * @return Routine to determine if book exists within either catalog or
    * repository
    */
  def bookExists(
    catalog: Catalog,
    title: Titles,
    author: Authors
  ): BookCatalogQuery[Boolean]

  /**
    * Determine if book with given ISBN exists within catalog
    * @param catalog Catalog being queried
    * @param isbn ISBN of book being examined
    * @return Routine to determine if book exists within either catalog or
    * repository
    */
  def bookExists(
    catalog: Catalog,
    isbn: ISBNs
  ): BookCatalogQuery[Boolean]
}
