package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

import scala.collection.Set

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Verifies adding books to book catalog repository
  */
class AddingBookSpec
    extends Specification
    with ScalaCheck {
  private case class TestBook(
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    coverImage: CoverImages,
    categories: Set[Categories]
  ) extends Book {
  }

  private val emptyBook =
    TestBook(
      "",
      "",
      "",
      None,
      None,
      Set()
    )

  "Adding valid books to the repository" >> {
    "updates the repository" >> {
      val database =
        new StubDatabase
      BookCatalogRepositoryInterpreter.setConnection(
        database.connectionTransactor
      )
      val bookToStore =
        TestBook(
          "Ground Zero",
          "Kevin J. Anderson",
          "006105223X",
          Some(
            "Description for Ground Zero"
          ),
          Some(
            getClass.getResource(
              "/GroundZero.jpg"
            ).toURI
          ),
          Set[Categories](
            "sci-fi",
            "conspiracy"
          )
        )

      val saveResult =
        BookCatalogRepositoryInterpreter add bookToStore
      saveResult must beRight
    }

    "places the book into the repository" >> pending
  }

  "Trying to add books with no title to the repository" >> {
    "indicates the repository was not updated" >> pending
  }

  "Trying to add books with no author to the repository" >> {
    "indicates the repository was not updated" >> pending
  }

  "Trying to add books with the same title and author as a book in the repository" >> {
    "indicates the repository was not updated" >> pending
  }

  "Trying to add books with no ISBN to the repository" >> {
    "indicates the repository was not updated" >> pending
  }

  "Trying to add books with the same ISBN as a book in the repository" >> {
    "indicates the repository was not updated" >> pending
  }
}
