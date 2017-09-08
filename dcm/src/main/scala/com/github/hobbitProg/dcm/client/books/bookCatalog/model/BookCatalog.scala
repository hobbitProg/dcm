package com.github.hobbitProg.dcm.client.books.bookCatalog.model

import scala.collection.Set
import scala.util.Try

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * Algebra for catalog containing books
  * @author Kyle Cranmer
  * @since 0.1
  */
trait BookCatalog {
  /**
    * Place new book into catalog
    * @param title Title of new book
    * @param author Author of new book
    * @param isbn ISBN of new book
    * @param description Description of new book
    * @param cover Cover image of new book
    * @param categories Categories of new book
    * @return Function that places book into catalog
    */
  def add(
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    cover: CoverImages,
    categories: Set[Categories]
  ): Try[BookCatalog]

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
//  def update(
//    originalBook: Book,
//    updatedTitle: Titles,
//    updatedAuthor: Authors,
//    updatedISBN: ISBNs,
//    updatedDescription: Description,
//    updatedCover: CoverImages,
//    updatedCategories: Set[Categories]
//  ): Reader[BookRepository, Try[Book]]

  /**
    * Register action to perform when book is added to catalog
    * @param addAction Action to perform
    * @param Book catalog with action within add subscribers
    */
  def onAdd(
    addAction: Book => Unit
  ): BookCatalog

  /**
    * Register action to perform when book is added to catalog
    * @param modifyAction Action to perform
    */
//  def onModify(
//    modifyAction: (Book, Book) => Unit
//  ): Unit

  /**
    * Determine if book with given title and author already exists in book
    * catalog
    * @param title Title of book being examined
    * @param author Author of book being examined
    * @return True if book with given title and author already exists in book
    * catalog and false otherwise
    */
//  def existsInCatalog(
//    title: Titles,
//    author: Authors
//  ): Reader[BookRepository, Boolean]

  /**
    * Determine if book with given ISBN already exists in book catalog
    * @param isbn ISBN of book being examined
    * @return True if book with given ISBN already exists in book catalog and
    * false otherwise
    */
//  def existsInCatalog(
//    isbn: ISBNs
//  ): Reader[BookRepository, Boolean]

  /**
    * Attempt to retrieve book from catalog by ISBN
    * @param isbn ISBN of book to retrieve
    * @return Either success with book from catalog if book with given ISBN
    *         exists within catalog and Failure otherwise
    */
  def getByISBN(
    desiredISBN: ISBNs
  ): Try[Book]
}
