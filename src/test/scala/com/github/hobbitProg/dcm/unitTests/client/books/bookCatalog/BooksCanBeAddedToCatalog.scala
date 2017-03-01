package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog

import java.net.URI

import org.scalamock.scalatest.MockFactory

import org.scalatest.FreeSpec
import org.scalatest.Matchers._

import scala.collection.Set
import scala.util.matching.Regex

import com.github.hobbitProg.dcm.client.books._
import com.github.hobbitProg.dcm.client.books.Conversions._
import com.github.hobbitProg.dcm.client.books.bookCatalog.{Book, Catalog}
import com.github.hobbitProg.dcm.client.books.bookCatalog.Implicits._
import com.github.hobbitProg.dcm.client.books.bookCatalog.storage.Storage

/**
  * Verifies books can be added to catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class BooksCanBeAddedToCatalog
  extends FreeSpec
    with MockFactory {
  "Given a book catalog" - {
    val catalogStorage =
      stub[Storage]
    val originalBookCatalog: Catalog =
      new Catalog(
        catalogStorage
      )

    "and a book containing all needed information to add to the catalog" - {
      val newBook: Book =
        (
          "Ground Zero",
          "Kevin J. Anderson",
          "006105223X",
          Some("Description for Ground Zero"),
          Some[URI](
            getClass.getResource(
              "/GroundZero.jpg"
            ).toURI
          ),
          Set[String](
            "sci-fi",
            "conspiracy"
          )
        )
      (catalogStorage.save _).when(
        newBook
      ).returns(
        Some(catalogStorage)
      )

      "and a listener for book addition events" - {
        var bookThatWasBroadcast: Book = null

        //noinspection ScalaUnusedSymbol
        val additionListener: Catalog.Subscriptions =
          originalBookCatalog onAdd {
            addedBook =>
            bookThatWasBroadcast = addedBook
          }

        "when the book is added to the catalog" - {
          val updatedBookCatalog =
            originalBookCatalog + newBook

          "then the book is added to the catalog" in {
            (catalogStorage.save _).verify(
              newBook
            )
            updatedBookCatalog shouldBe defined
          }

          "and the book is given to the listener" in {
            bookThatWasBroadcast shouldEqual newBook
          }
        }
      }
    }
  }

  "Given a book catalog" - {
    val catalogStorage =
      stub[Storage]
    val originalBookCatalog: Catalog =
      new Catalog(
        catalogStorage
      )

    "and a book with no title" - {
      val newBook: Book =
        (
          "",
          "Kevin J. Anderson",
          "006105223X",
          Some("Description for Ground Zero"),
          Some[URI](
            getClass.getResource(
              "/GroundZero.jpg"
            ).toURI
          ),
          Set[String](
            "sci-fi",
            "conspiracy"
          )
        )
      (catalogStorage.save _).when(
        newBook
      ).returns(
        None
      )

      "and a listener for book addition events" - {
        var bookThatWasBroadcast: Book =
          null
        //noinspection ScalaUnusedSymbol
        val additionListener: Catalog.Subscriptions =
          originalBookCatalog onAdd {
            addedBook =>
              bookThatWasBroadcast = addedBook
          }

        "when the book is tried to be placed into the catalog" - {
          val updatedBookCatalog =
            originalBookCatalog + newBook

          "then the book is not placed into the catalog" in {
            updatedBookCatalog shouldBe None
          }

          "and the book is not given to the listener" in {
            bookThatWasBroadcast shouldBe null
          }
        }
      }
    }
  }

  "Given a book catalog" - {
    val catalogStorage =
      stub[Storage]
    val originalBookCatalog: Catalog =
      new Catalog(
        catalogStorage
      )

    "and a book with no author" - {
      val newBook: Book =
        (
          "Ground Zero",
          "",
          "006105223X",
          Some("Description for Ground Zero"),
          Some[URI](
            getClass.getResource(
              "/GroundZero.jpg"
            ).toURI
          ),
          Set[String](
            "sci-fi",
            "conspiracy"
          )
        )
      (catalogStorage.save _).when(
        newBook
      ).returns(
        None
      )

      "and a listener for book addition events" - {
        var bookThatWasBroadcast: Book = null

        //noinspection ScalaUnusedSymbol
        val additionListener: Catalog.Subscriptions =
          originalBookCatalog onAdd {
            addedBook =>
              bookThatWasBroadcast = addedBook
          }

        "when the book is tried to be placed into the catalog" - {
          val updatedBookCatalog =
            originalBookCatalog + newBook

          "then the book is not placed into the catalog" in  {
            updatedBookCatalog shouldBe None
          }

          "and the book is not given to the listener" in {
            bookThatWasBroadcast shouldBe null
          }
        }
      }
    }
  }

  "Given a book catalog" - {
    val catalogStorage =
      stub[Storage]
    val originalBookCatalog: Catalog =
      new Catalog(
        catalogStorage
      )

    "and a book with no ISBN" - {
      val newBook: Book =
        (
          "Ground Zero",
          "Kevin J. Anderson",
          "",
          Some("Description for Ground Zero"),
          Some[URI](
            getClass.getResource(
              "/GroundZero.jpg"
            ).toURI
          ),
          Set[String](
            "sci-fi",
            "conspiracy"
          )
        )
      (catalogStorage.save _).when(
        newBook
      ).returns(
        None
      )

      "and a listener for book addition events" - {
        var bookThatWasBroadcast: Book = null

        //noinspection ScalaUnusedSymbol
        val additionListener: Catalog.Subscriptions =
          originalBookCatalog onAdd {
            addedBook =>
              bookThatWasBroadcast = addedBook
          }

        "when the book is tried to be placed into the catalog" - {
          val updatedBookCatalog =
            originalBookCatalog + newBook

          "then the book is not placed into the catalog" in {
            updatedBookCatalog shouldBe None
          }

          "and the book is not given to the listener" in {
            bookThatWasBroadcast shouldBe null
          }
        }
      }
    }
  }

  "Given a populated book catalog" - {
    val catalogStorage =
      stub[Storage]
    val originalBookCatalog: Catalog =
      new Catalog(
        catalogStorage
      )

    "and a book containing the same title/author pair as a book already in " +
    "the catalog" - {
      val newBook: Book =
        (
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
        (catalogStorage.save _).when(
          newBook
        ).returns(
          None
        )

      "and a listener for book addition events" - {
        var bookThatWasBroadcast: Book = null
        val additionListener: Catalog.Subscriptions =
          originalBookCatalog onAdd {
            addedBook =>
            bookThatWasBroadcast = addedBook
          }

        "when the book is placed into the catalog" - {
          val updatedBookCatalog =
            originalBookCatalog + newBook

          "then the book is not placed into the catalog" in {
            updatedBookCatalog shouldBe None
          }

          "and the book is not given to the listener" in {
            bookThatWasBroadcast shouldBe null
          }
        }
      }
    }
  }
}
