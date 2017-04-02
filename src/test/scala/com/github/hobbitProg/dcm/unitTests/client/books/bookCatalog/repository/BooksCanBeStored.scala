package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import cats.data.Validated._

import org.scalatest.{FreeSpec, Matchers}

import scala.collection.Set
import scala.math.Ordering._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.interpreter.DatabaseBookRepositoryInterpreter

/**
  * Verifies books can be stored into repository
  * @author Kyle Cranmer
  * @since 0.1
  */
class BooksCanBeStored
  extends FreeSpec
    with Matchers {
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

  "Given storage to place books into" - {
    val database =
      new StubDatabase
    DatabaseBookRepositoryInterpreter.setConnection(
      database.connectionTransactor
    )

    "and a book containing all required information to place into storage" - {
      val bookToStore =
        Book.book(
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

      "when the book is placed into storage" - {
        val saveResult =
          DatabaseBookRepositoryInterpreter.save(
            bookToStore getOrElse emptyBook
          )

        "then storage is updated" in {
          saveResult shouldBe ('right)
        }

        "and the book is placed into storage" in {
          val enteredBook =
            Book.book(
              database.addedTitle,
              database.addedAuthor,
              database.addedISBN,
              database.addedDescription,
              database.addedCover,
              database.addedCategoryAssociations map {
                categoryAssociation =>
                categoryAssociation._2
              }
            )
          enteredBook shouldEqual bookToStore
          (database.addedCategoryAssociations map {
            categoryAssociation =>
              categoryAssociation._1
          }) shouldEqual Set[ISBNs](
            (bookToStore getOrElse emptyBook).isbn
          )
        }
      }
    }
  }

  "Given storage to place books into" - {
    val database =
      new StubDatabase
    DatabaseBookRepositoryInterpreter.setConnection(
      database.connectionTransactor
    )

    "and a book without a title to place into storage" - {
      val bookToStore: Book =
        TestBook(
          "",
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

      "when the book is placed into storage" - {
        val saveResult =
          DatabaseBookRepositoryInterpreter.save(
            bookToStore
          )

        "then the book is not placed into storage" in {
          saveResult shouldBe ('left)
        }
      }
    }
  }

  "Given storage to place books into" - {
    val database =
      new StubDatabase
    DatabaseBookRepositoryInterpreter.setConnection(
      database.connectionTransactor
    )

    "and a book without an author to place into storage" - {
      val bookToStore: Book =
        TestBook(
          "Ground Zero",
          "",
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

      "when the book is placed into storage" - {
        val saveResult =
          DatabaseBookRepositoryInterpreter.save(
            bookToStore
          )

        "then the book is not placed into storage" in {
          saveResult shouldBe ('left)
        }
      }
    }
  }

  "Given storage to place books into" - {
    val database =
      new StubDatabase
    DatabaseBookRepositoryInterpreter.setConnection(
      database.connectionTransactor
    )

    "and a book without an ISBN to place into storage" - {
      val bookToStore: Book =
        TestBook(
          "Ground Zero",
          "Kevin J. Anderson",
          "",
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

      "when the book is placed into storage" - {
        val saveResult =
          DatabaseBookRepositoryInterpreter.save(
            bookToStore
          )

        "then the book is not placed into storage" in {
          saveResult shouldBe ('left)
        }
      }
    }
  }

  "Given storage to place books into (with books already in storage)" - {
    val database =
      new StubDatabase
    DatabaseBookRepositoryInterpreter.setConnection(
      database.connectionTransactor
    )

    "and a book containing the same name and author as a book that is " +
    "already in storage" - {
      val bookToStore: Book =
        TestBook(
          "Ruins",
          "Kevin J. Anderson",
          "006105223X",
          Some(
            "Description for Ruins"
          ),
          Some(
            getClass.getResource(
              "/Ruins.jpg"
            ).toURI
          ),
          Set[Categories](
            "sci-fi",
            "conspiracy"
          )
        )

      "when the book is placed into storage" - {
        val saveResult =
          DatabaseBookRepositoryInterpreter.save(
            bookToStore
          )

        "then the book is not placed into storage" in {
          saveResult shouldBe ('left)
        }
      }
    }
  }
}