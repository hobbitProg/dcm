package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogControl

import cats.data.Reader

import scala.collection.Set
import scala.util.{Success, Failure, Try}

import com.github.hobbitProg.dcm.client.books.bookCatalog.service.BookCatalog

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository

/**
  * Test interpreter for book catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class TestCatalog
    extends BookCatalog {
  private class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book

  // Subscribers for adding books to catalog
  var addSubscribers =
    Set[Book => Unit]()

  // Subscribers for modifying books within catalog
  var modifySubscribers =
    Set[(Book, Book) => Unit]()

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
  ): Reader[BookRepository, Try[Book]] = {
    Reader {
      repository: BookRepository =>
      val newBook =
        new TestBook(
          title,
          author,
          isbn,
          description,
          cover,
          categories
        )
      addSubscribers foreach {
        action =>
        action(
          newBook
        )
      }
      Success(
        newBook
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
      repository =>
        val modifiedBook =
          new TestBook(
            updatedTitle,
            updatedAuthor,
            updatedISBN,
            updatedDescription,
            updatedCover,
            updatedCategories
          )
        modifySubscribers.foreach {
          action =>
          action(
            originalBook,
            modifiedBook
          )
        }
      Success(
        modifiedBook
      )
    }
  }

  /**
    * Register action to perform when book is added to catalog
    * @param addAction Action to perform
    */
  def onAdd(
    addAction: Book => Unit
  ): Unit = {
    addSubscribers =
      addSubscribers + addAction
  }

  /**
    * Register action to perform when book is added to catalog
    * @param modifyAction Action to perform
    */
  def onModify(
    modifyAction: (Book, Book) => Unit
  ): Unit = {
    modifySubscribers =
      modifySubscribers + modifyAction
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
  ): Reader[BookRepository, Boolean] = {
    Reader {
      repository: BookRepository =>
      false
    }
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
      false
    }
  }
}
