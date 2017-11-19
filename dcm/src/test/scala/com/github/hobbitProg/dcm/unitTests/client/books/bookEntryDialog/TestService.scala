package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import scala.collection.Set

import cats.Id
import cats.data.Kleisli
import cats.data.Validated.Valid

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService

class TestService
    extends BookCatalogService[BookCatalog] {
  var newTitle: Titles = _
  var newAuthor: Authors = _
  var newISBN: ISBNs = _
  var newDescription: Description = _
  var newCover: CoverImages = _
  var newCategories: Set[Categories] = _

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
    catalog: BookCatalog,
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    cover: CoverImages,
    categories: Set[Categories]
  ): BookCatalogOperation[BookCatalog] = Kleisli {
    repository: BookCatalogRepository =>
    newTitle = title
    newAuthor = author
    newISBN = isbn
    newDescription = description
    newCover = cover
    newCategories = categories

    Valid(catalog)
  }

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
    catalog: BookCatalog,
    originalBook: Book,
    newTitle: Titles,
    newAuthor: Authors,
    newISBN: ISBNs,
    newDescription: Description,
    newCover: CoverImages,
    newCategories: Set[Categories]
  ): BookCatalogOperation[BookCatalog] = Kleisli {
    repository: BookCatalogRepository =>
    Valid(catalog)
  }

  /**
    * Determine if book with given title and author exists within catalog
    * @param catalog Catalog being queried
    * @param title Title of book being examined
    * @param author Author of book being examined
    * @return Routine to determine if book exists within either catalog or
    * repository
    */
  def bookExists(
    catalog: BookCatalog,
    title: Titles,
    author: Authors
  ): BookCatalogQuery[Boolean] = Kleisli[Id, BookCatalogRepository, Boolean] {
    repository: BookCatalogRepository =>
    false
  }

  /**
    * Determine if book with given ISBN exists within catalog
    * @param catalog Catalog being queried
    * @param isbn ISBN of book being examined
    * @return Routine to determine if book exists within either catalog or
    * repository
    */
  def bookExists(
    catalog: BookCatalog,
    isbn: ISBNs
  ): BookCatalogQuery[Boolean] = Kleisli[Id, BookCatalogRepository, Boolean] {
    repository: BookCatalogRepository =>
    false
  }
}
