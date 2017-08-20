package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import cats.data.Reader

import scala.collection.Set
import scala.util.{Success, Failure, Try}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.BookCatalog

/**
  * Book catalog interpreter for verifying book entry dialog
  * @author Kyle Cranmer
  * @since 0.1
  */
class TestCatalog
    extends BookCatalog {
  // Information on book added to catalog
  var newTitle: Titles = _
  var newAuthor: Authors = _
  var newISBN: ISBNs = _
  var newDescription: Description = _
  var newCover: CoverImages = _
  var newCategories: Set[Categories] = _

  // Information on book removed from catalog
  var removedISBN: ISBNs = _

  /**
    * Place new book into catalog
    * @param title Title of new book
    * @param author Author of new book
    * @param isbn ISBN of new book
    * @param description Description of new book
    * @param cover Cover image of new book
    * @param categories Categories of new book
    * @return Function that places book into catalog repository
    */
  def add(
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    cover: CoverImages,
    categories: Set[Categories]
  ): Reader[BookRepository, Try[Book]] =
    Reader {
      repository => {
        newTitle = title
        newAuthor = author
        newISBN = isbn
        newDescription = description
        newCover = cover
        newCategories = categories
        Success(
          Book.book(
            title,
            author,
            isbn,
            description,
            cover,
            categories
          ).getOrElse(
            null
          )
        )
      }
    }

  /**
    * Replace original version of book with updated version
    * @param originalBook Book that is being modified
    * @param updatedTitle New title of book
    * @param updatedAuthor New author of book
    * @param updatedISBN New ISBN of book
    * @param updatedDescription New description of book
    * @param updatedCover New cover of book
    * @param updatedCategories New categories associated wityh book
    */
  def update(
    originalBook: Book,
    updatedTitle: Titles,
    updatedAuthor: Authors,
    updatedISBN: ISBNs,
    updatedDescription: Description,
    updatedCover: CoverImages,
    updatedCategories: Set[Categories]
  ): Reader[BookRepository, Try[Book]] = {
    Reader {
      repository => {
        newTitle = updatedTitle
        newAuthor = updatedAuthor
        newISBN = updatedISBN
        newDescription = updatedDescription
        newCover = updatedCover
        newCategories = updatedCategories
        removedISBN = originalBook.isbn
        Success(
          Book.book(
            updatedTitle,
            updatedAuthor,
            updatedISBN,
            updatedDescription,
            updatedCover,
            updatedCategories
          ).getOrElse(
            null
          )
        )
      }
    }
  }

  /**
    * Register action to perform when book is added to catalog
    * @param addAction Action to perform
    */
  def onAdd(
    addAction: Book => Unit
  ): Unit = {
  }

  /**
    * Register action to perform when book is added to catalog
    * @param modifyAction Action to perform
    */
  def onModify(
    modifyAction: (Book, Book) => Unit
  ): Unit = {
  }

  /**
    * Determine if book with given title and author already exists in book
    * catalog
    * @param title Title of book being examined
    * @param author Author of book being examined
    * @return True if book with given title and author already exists in book
    * catalog and false otherwise
    */
  def existsInCatalog(
    title: Titles,
    author: Authors
  ): Reader[BookRepository, Boolean] =
    Reader {
      repository =>
      title == "Ruins" &&
      author == "Kevin J. Anderson"
    }

  /**
    * Determine if book with given ISBN already exists in book catalog
    * @param isbn ISBN of book being examined
    * @return True if book with given ISBN already exists in book catalog and
    * false otherwise
    */
  def existsInCatalog(
    isbn: ISBNs
  ): Reader[BookRepository, Boolean] = {
    Reader {
      repository: BookRepository =>
        isbn == "0061052477" ||
      isbn == "0061054143"
    }
  }
}
