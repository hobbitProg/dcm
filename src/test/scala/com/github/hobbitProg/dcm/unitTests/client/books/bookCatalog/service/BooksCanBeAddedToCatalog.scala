package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service

import org.scalatest.{FreeSpec, Matchers}

import scala.collection.Set
import scala.util.Success

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
            resultingBook shouldBe an[Success[Book]]
            resultingBook.get.title shouldBe newTitle
            resultingBook.get.author shouldBe newAuthor
            resultingBook.get.isbn shouldBe newISBN
            resultingBook.get.description shouldBe newDescription
            resultingBook.get.coverImage shouldBe newCover
            resultingBook.get.categories shouldBe newCategories
            testRepository.savedBook shouldBe resultingBook.get
          }
          "and the book is given to the listener" in pending
        }
      }
    }
  }
}
