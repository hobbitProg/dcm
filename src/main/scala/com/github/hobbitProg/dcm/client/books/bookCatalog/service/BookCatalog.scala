package com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter

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
}
