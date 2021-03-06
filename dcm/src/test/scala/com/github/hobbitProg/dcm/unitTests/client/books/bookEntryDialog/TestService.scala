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
  var existingTitle: Titles = _
  var existingAuthor: Authors = _
  var existingISBN: ISBNs = _

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
  ): BookCatalogOperation[(BookCatalog, BookCatalogRepository)] = Kleisli {
    repository: BookCatalogRepository =>
    val data: TestService.BookData =
      new TestService.BookData(
        title,
        author,
        isbn,
        description,
        cover,
        categories
      )

    for (callback <- newBookCallbacks) {
      callback(
        data
      )
    }

    Valid((catalog, repository))
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
  ): BookCatalogOperation[(BookCatalog, BookCatalogRepository)] = Kleisli {
    repository: BookCatalogRepository =>
    val data: TestService.BookData =
      new TestService.BookData(
        newTitle,
        newAuthor,
        newISBN,
        newDescription,
        newCover,
        newCategories
      )

    for (callback <- modifyBookCallback) {
      callback(
        originalBook,
        data
      )
    }

    Valid((catalog, repository))
  }

  /**
    * Delete the given book from the catalog
    * @param catalog Catalog being modified
    * @param title The title of the book to remove
    * @param author The author of the book to remove
    * @return Routine to modify book in catalog and repository
    */
  def delete(
    catalog: BookCatalog,
    title: Titles,
    author: Authors
  ): BookCatalogOperation[(BookCatalog, BookCatalogRepository)] =  Kleisli {
    repository: BookCatalogRepository =>
    Valid((catalog, repository))
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
    title == existingTitle &&
    author == existingAuthor
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
    existingISBN == isbn
  }

  /**
    * Add a callback to perform when adding a new book to the catalog
    *
    * @param callback The callback to execute
    */
  def onAdd(
    callback: TestService.BookData => Unit
  ) =
    newBookCallbacks =
      newBookCallbacks + callback

  /**
    * Add a callback to perform when modifying a book in the catalog
    *
    * @param callback The callback to execute
    */
  def onModify(
    callback: (Book, TestService.BookData) => Unit
  ) =
    modifyBookCallback =
      modifyBookCallback + callback

  // Callbacks for when a book is added to the catalog
  private var newBookCallbacks: Set[TestService.BookData => Unit] =
    Set[TestService.BookData => Unit]()

  // Callbacks for when a book in the catalog is modified
  private var modifyBookCallback: Set[(Book, TestService.BookData) => Unit] =
    Set[(Book, TestService.BookData) => Unit]()
}

object TestService {
  type BookData = (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories])
}
