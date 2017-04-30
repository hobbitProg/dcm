package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model

import cats.data.Validated
import Validated._
import cats.scalatest.ValidatedMatchers

import java.net.URI

import org.scalatest.{FreeSpec, Matchers}

import scala.Some
import scala.collection.Set

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * Verifies books can be created
  * @author Kyle Cranmer
  */
class OnlyValidBooksCanBeCreated
    extends FreeSpec
    with Matchers
    with ValidatedMatchers {
  "Given information on a book with all valid information" - {
    val title: Titles = "Ruins"
    val author: Authors = "Kevin J. Anderson"
    val isbn: ISBNs = "0061052477"
    val description: Description = Some("Description for Ruins")
    val coverImage: CoverImages =
      Some(getClass.getResource("/Ruins.jpg").toURI)
    val categories: Set[Categories] =
      Set(
        "sci-fi",
        "conspiracy"
      )

    "when a new book is requested" - {
      val newBook: Validated[String, Book] =
        Book.book(
          title,
          author,
          isbn,
          description,
          coverImage,
          categories
        )

      "then a book is created" in {
        newBook shouldBe valid
      }
      "and the book has the given title" in {
        newBook match {
          case Valid(createdBook) =>
            createdBook.title shouldBe title
          case Invalid(errorMessage) =>
            fail(
              "Book was not created"
            )
        }
      }

      "and the book has the given author" in {
        newBook match {
          case Valid(createdBook) =>
            createdBook.author shouldBe author
          case Invalid(errorMessage) =>
            fail(
              "Book was not created"
            )
        }
      }

      "and the book has the given ISBN" in {
        newBook match {
          case Valid(createdBook) =>
            createdBook.isbn shouldBe isbn
          case Invalid(errorMessage) =>
            fail(
              "Book was not created"
            )
        }
      }

      "and the book has the given description" in {
        newBook match {
          case Valid(createdBook) =>
            createdBook.description shouldBe description
          case Invalid(errorMessage) =>
            fail(
              "Book was not created"
            )
        }
      }

      "and the book has the given cover image" in {
        newBook match {
          case Valid(createdBook) =>
            createdBook.coverImage shouldBe coverImage
          case Invalid(errorMessage) =>
            fail(
              "Book was not created"
            )
        }
      }

      "and the book has the given categories" in {
        newBook match {
          case Valid(createdBook) =>
            createdBook.categories shouldBe categories
          case Invalid(errorMessage) =>
            fail(
              "Book was not created"
            )
        }
      }
    }
  }

  "Given information on a book with no title" - {
    val title: Titles = ""
    val author: Authors = "Kevin J. Anderson"
    val isbn: ISBNs = "0061052477"
    val description: Description = Some("Description for Ruins")
    val coverImage: CoverImages =
      Some(getClass.getResource("/Ruins.jpg").toURI)
    val categories: Set[Categories] =
      Set(
        "sci-fi",
        "conspiracy"
      )

    "when a new book is requested" - {
      val newBook: Validated[String, Book] =
        Book.book(
          title,
          author,
          isbn,
          description,
          coverImage,
          categories
        )

      "then no book is created" in {
        newBook shouldBe invalid
      }
    }
  }

  "Given information on a book with no author" - {
    val title: Titles = "Ruins"
    val author: Authors = ""
    val isbn: ISBNs = "0061052477"
    val description: Description = Some("Description for Ruins")
    val coverImage: CoverImages =
      Some(getClass.getResource("/Ruins.jpg").toURI)
    val categories: Set[Categories] =
      Set(
        "sci-fi",
        "conspiracy"
      )

    "when a new book is requested" - {
      val newBook: Validated[String, Book] =
        Book.book(
          title,
          author,
          isbn,
          description,
          coverImage,
          categories
        )

      "then no book is created" in {
        newBook shouldBe invalid
      }
    }
  }

  "Given information on a book with no ISBN" - {
    val title: Titles = "Ruins"
    val author: Authors = "Kevin J. Anderson"
    val isbn: ISBNs = ""
    val description: Description = Some("Description for Ruins")
    val coverImage: CoverImages =
      Some(getClass.getResource("/Ruins.jpg").toURI)
    val categories: Set[Categories] =
      Set(
        "sci-fi",
        "conspiracy"
      )

    "when a new book is requested" - {
      val newBook: Validated[String, Book] =
        Book.book(
          title,
          author,
          isbn,
          description,
          coverImage,
          categories
        )

      "then no book is created" in {
        newBook shouldBe invalid
      }
    }
  }
}
