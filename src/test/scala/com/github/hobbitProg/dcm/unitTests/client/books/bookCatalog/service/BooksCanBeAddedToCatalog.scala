package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service

import org.scalatest.{FreeSpec, Matchers}

import scala.collection.Set
import scala.util.{Failure, Success}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.Book
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.BookCatalogInterpreter._

class BooksCanBeAddedToCatalog
    extends FreeSpec
    with Matchers {
  "Given a repository that contains the catalog" - {
    val testRepository =
      new FakeRepository

    "and all of the information on a book to add to the catalog" - {
      val newTitle =
        "Ground Zero"
      val newAuthor =
        "Kevin J. Anderson"
      val newISBN =
        "006105223X"
      val newDescription =
        Some(
          "Description for Ground Zero"
        )
      val newCover =
        Some(
          getClass().
            getResource(
              "/GroundZero.jpg"
            ).toURI
        )
      val newCategories =
        Set(
          "sci-fi",
          "conspiracy"
        )

      "and a listener for book addition events" - {
        var sentBook: Book = null
        onAdd(
          addedBook => sentBook = addedBook
        )

        "when the book is added to the catalog" - {
          val resultingBook =
            add(
              newTitle,
              newAuthor,
              newISBN,
              newDescription,
              newCover,
              newCategories
            )(
              testRepository
            )

          "then the book is added to the catalog" in {
            resultingBook should be (an[Success[_]])
            resultingBook.get.title should be (newTitle)
            resultingBook.get.author should be (newAuthor)
            resultingBook.get.isbn should be (newISBN)
            resultingBook.get.description should be (newDescription)
            resultingBook.get.coverImage should be (newCover)
            resultingBook.get.categories should be (newCategories)
            testRepository.savedBook should be (resultingBook.get)
          }

          "and the book is given to the listener" in {
            sentBook should not be (null)
            sentBook.title should be (newTitle)
            sentBook.author should be (newAuthor)
            sentBook.isbn should be (newISBN)
            sentBook.description should be (newDescription)
            sentBook.coverImage should be (newCover)
            sentBook.categories should be (newCategories)
          }
        }
      }
    }
  }

  "Given a repository that contains the catalog" - {
    val testRepository =
      new FakeRepository

    "and the information on a book with no title" - {
      val newTitle =
        ""
      val newAuthor =
        "Kevin J. Anderson"
      val newISBN =
        "006105223X"
      val newDescription =
        Some(
          "Description for Ground Zero"
        )
      val newCover =
        Some(
          getClass().
            getResource(
              "/GroundZero.jpg"
            ).toURI
        )
      val newCategories =
        Set(
          "sci-fi",
          "conspiracy"
        )

      "and a listener for book addition events" - {
        var sentBook: Book = null
        onAdd(
          addedBook => sentBook = addedBook
        )

        "when the book information is attempted to be added to the catalog" - {
          val resultingBook =
            add(
              newTitle,
              newAuthor,
              newISBN,
              newDescription,
              newCover,
              newCategories
            )(
              testRepository
            )

          "then the book is not placed into the catalog" in {
            resultingBook should be (a[Failure[_]])
          }

          "and the book is not given to the listener" in {
            sentBook should be (null)
          }
        }
      }
    }
  }

  "Given a repository that contains the catalog" - {
    "and information on a book with no author" - {
      "and a listener for book addition events" - {
        "when the book information is attempted to be added to the catalog" - {
          "then the book is not placed into the catalog" in pending
          "and the book is not given to the listener" in pending
        }
      }
    }
  }

  "Given a repository that contains the catalog" - {
    "and information on a book with no ISBN" - {
      "and a listener for book addition events" - {
        "when the book information is attempted to be added to the catalog" - {
          "then the book is not placed into the catalog" in pending
          "and the book is not given to the listener" in pending
        }
      }
    }
  }
}
