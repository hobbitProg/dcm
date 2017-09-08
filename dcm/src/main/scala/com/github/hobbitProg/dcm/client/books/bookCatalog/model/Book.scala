package com.github.hobbitProg.dcm.client.books.bookCatalog.model

import cats.data.Validated
import Validated._

import scala.collection.Set

/**
  * Model of book to place into catalog
  */
trait Book {
  def title: Titles
  def author: Authors
  def isbn: ISBNs
  def description: Description
  def coverImage: CoverImages
  def categories: Set[Categories]

  override def equals(
    obj: Any
  ): Boolean = {
    // Ensure other object is a book
    if (!obj.isInstanceOf[Book]) {
      return false
    }

    // Ensure all fields are the same
    val otherBook: Book = obj.asInstanceOf[Book]

    title == otherBook.title &&
    author == otherBook.author &&
    isbn == otherBook.isbn &&
    description == otherBook.description &&
    coverImage == otherBook.coverImage &&
    categories == otherBook.categories
  }
}

object Book {
  /**
    * Verify information to place in new book
    * @param title Title of new book
    * @param author Author of new book
    * @param isbn ISBN of new book
    * @return True if given information is valid and false otherwise
    */
  private def isValid(
    title: Titles,
    author: Authors,
    isbn: ISBNs
  ): Boolean = {
    title != "" &&
    author != "" &&
    isbn != ""
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
          title,
          author,
          isbn
        )) {
      Valid(
        BookImpl(
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

  private case class BookImpl(
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    coverImage: CoverImages,
    categories: Set[Categories]
  ) extends Book {
  }
}
