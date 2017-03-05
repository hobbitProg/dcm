package com.github.hobbitProg.dcm.client.books.bookCatalog.model

import cats.data.Validated
import Validated._

import scala.collection.Set

/**
  * Model of book to place into catalog
  */
case class Book (
  title: Titles,
  author: Authors,
  isbn: ISBNs,
  description: Description,
  coverImage: CoverImages,
  categories: Set[Categories]
) {
}

object Book {
  def book(
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    coverImage: CoverImages,
    categories: Set[Categories]
  ): Validated[String, Book] = {
    Valid(
      Book(
        title,
        author,
        isbn,
        description,
        coverImage,
        categories
      )
    )
  }
}
