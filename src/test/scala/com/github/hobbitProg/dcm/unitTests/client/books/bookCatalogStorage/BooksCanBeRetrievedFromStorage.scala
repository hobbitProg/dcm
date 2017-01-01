package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogStorage

import acolyte.jdbc.{AcolyteDSL, StatementHandler, UpdateExecution, Driver => AcolyteDriver}
import acolyte.jdbc.Implicits._

import doobie.imports._

import org.scalatest.FreeSpec

import scala.collection.Set

import scalaz.concurrent.Task

import com.github.hobbitProg.dcm.client.books.Categories
import com.github.hobbitProg.dcm.client.books.bookCatalog.Book
import com.github.hobbitProg.dcm.client.books.bookCatalog.Implicits._
import com.github.hobbitProg.dcm.client.books.bookCatalog.storage.Storage

/**
  * Verifies books can be retrieved from storage
  * @author Kyle Cranmer
  * @since 0.1
  */
class BooksCanBeRetrievedFromStorage
  extends FreeSpec {
  // Contents of book storage
  val definedBooks: Set[Book] =
    Set[Book](
      (
        "Ruins",
        "Kevin J. Anderson",
        "0061052477",
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
      ),
      (
        "Goblins",
        "Charles Grant",
        "0061054143",
        Some(
          "Description for Goblins"
        ),
        Some(
          getClass.getResource(
            "/Goblins.jpg"
          ).toURI
        ),
        Set[Categories](
          "sci-fi",
          "conspiracy"
        )
      )
    )

  "Given populated book storage" - {
    AcolyteDriver.register(
      BooksCanBeRetrievedFromStorage.databaseId,
      bookStorageHandler
    )
    val connectionTransactor =
      DriverManagerTransactor[Task](
        "acolyte.jdbc.Driver",
        BooksCanBeRetrievedFromStorage.databaseURL
      )
    val bookStorage: Storage =
      Storage(
        connectionTransactor
      )

    "when books are requested from storage" - {
      "then books are retrieved from storage" in pending
    }
  }

  private def bookStorageHandler: StatementHandler =
    AcolyteDSL.handleStatement
}

object BooksCanBeRetrievedFromStorage {
  private val databaseId: String = "BookStorageTest"
  private  val databaseURL: String =
    "jdbc:acolyte:dcm-tests?handler=" + databaseId
}
