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

    "and a listener for book addition events" - {
      var bookThatWasBroadcast: Book =
        null
      //noinspection ScalaUnusedSymbol
      val additionListener: Catalog.Subscriptions =
        originalBookCatalog onAdd {
          addedBook =>
            bookThatWasBroadcast = addedBook
        }

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

        "when the book is added to the catalog" - {
          //noinspection ScalaUnusedSymbol
          val updatedBookCatalog =
            originalBookCatalog + newBook
          "then the book is added to the catalog" in {
            updatedBookCatalog shouldBe defined
            (catalogStorage.save _).verify(
              newBook
            )
          }
          "and the book is given to the listener" in {
            bookThatWasBroadcast shouldEqual newBook
          }
        }
      }
      "and a book with no defined to add to the catalog" - {
        "when the book is tried to place into the catalog" - {
          "then the book is not placed into the catalog" in pending
            "and the book is not given to the listener"
        }
      }
    }
  }
}
