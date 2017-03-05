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
  /**
    * Verify information to place in new book
    * @param title Title of new book
    * @return True if given information is valid and false otherwise
    */
  private def isValid(
    title: Titles
  ): Boolean = {
    title != ""
  }

  /**
    * Create new book
    * 
    * @param title Title of new book
    * @param author Author of new book
    * @param isbn ISBN of new book
    * @param description Description of new book
    * @param coverImage Image of book cover
    * @param categories Classification of book cover
    * 
    * @return Either new book or indication that given information is invalid
    */
  def book(
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    coverImage: CoverImages,
    categories: Set[Categories]
  ): Validated[String, Book] = {
    if (isValid(
          title
        )) {
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
    else {
      Invalid(
        "Given information is invalid for a book"
      )
    }
  }
}
