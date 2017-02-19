package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogStorage

import org.scalatest.{FreeSpec, Matchers}

import scala.collection.Set

import com.github.hobbitProg.dcm.client.books._
import com.github.hobbitProg.dcm.client.books.bookCatalog.Book
import com.github.hobbitProg.dcm.client.books.bookCatalog.Implicits._
import com.github.hobbitProg.dcm.client.books.bookCatalog.storage.Storage

/**
  * Verifies books can be placed into storage
  * @author Kyle Cranmer
  * @since 0.1
  */
class BooksCanBePlacedIntoStorage
  extends FreeSpec
    with Matchers {
  "Given storage to place books into" - {
    val database =
      new StubDatabase
    val bookStorage: Storage =
      Storage(
        database.connectionTransactor
      )

    "and a book containing all required information to place into storage" - {
      val bookToStore: Book =
        (
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
        val updatedStorage =
          bookStorage save bookToStore

        "then storage is updated" in {
          updatedStorage shouldBe defined
        }

        "and the book is placed into storage" in {
          val enteredBook: Book = (
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
          }) shouldEqual Set[ISBNs](bookToStore.isbn)
        }
      }
    }
  }

  "Given storage to place books into" - {
    val database =
      new StubDatabase
    val bookStorage: Storage =
      Storage(
        database.connectionTransactor
      )

    "and a book without a title to place into storage" - {
      val bookToStore: Book =
        (
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
        val updatedStorage =
          bookStorage save bookToStore

        "then the book is not placed into storage" in {
          updatedStorage shouldBe empty
        }
      }
    }
  }

  "Given storage to place books into" - {
    val database =
      new StubDatabase
    val bookStorage: Storage =
      Storage(
        database.connectionTransactor
      )

    "and a book without an author to place into storage" - {
      val bookToStore: Book =
        (
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
        val updatedStorage =
          bookStorage save bookToStore

        "then the book is not placed into storage" in {
          updatedStorage shouldBe empty
        }
      }
    }
  }

  "Given storage to place books into" - {
    val database =
      new StubDatabase
    val bookStorage: Storage =
      Storage(
        database.connectionTransactor
      )

    "and a book without an ISBN to place into storage" - {
      val bookToStore: Book =
        (
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
        val updatedStorage =
          bookStorage save bookToStore

        "then the book is not placed into storage" in {
          updatedStorage shouldBe empty
        }
      }
    }
  }
}
