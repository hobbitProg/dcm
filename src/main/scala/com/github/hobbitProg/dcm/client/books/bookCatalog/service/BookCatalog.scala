package com.github.hobbitProg.dcm.client.books.bookCatalog.service

import cats.data.Reader

import scala.collection.Set
import scala.util.Try

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository

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
    * @return Function that places book into catalog repository
    */
  def add(
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    cover: CoverImages,
    categories: Set[Categories]
  ): Reader[BookRepository, Try[Book]]

  /**
    * Register action to perform when book is added to catalog
    * @param addAction Action to perform
    */
  def onAdd(
    addAction: Book => Unit
  ): Unit

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
  ): Reader[BookRepository, Boolean]

  /**
    * Determine if book with given ISBN already exists in book catalog
    * @param isbn ISBN of book being examined
    * @return True if book with given ISBN already exists in book catalog and
    * false otherwise
    */
  def existsInCatalog(
    isbn: ISBNs
  ): Reader[BookRepository, Boolean]
}
