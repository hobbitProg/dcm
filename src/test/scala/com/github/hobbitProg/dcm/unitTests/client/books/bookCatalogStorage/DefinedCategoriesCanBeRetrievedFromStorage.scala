package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogStorage

import acolyte.jdbc.{AcolyteDSL, RowList1, Row1, StatementHandler, QueryExecution, Driver => AcolyteDriver}
import acolyte.jdbc.RowLists.rowList1
import acolyte.jdbc.Implicits._

import doobie.imports._

import org.scalatest.{FreeSpec, Matchers}

import scala.collection.Set

import scalaz.concurrent.Task

import com.github.hobbitProg.dcm.client.books.Categories
import com.github.hobbitProg.dcm.client.books.bookCatalog.storage.Storage

/**
  * Verifies defined book categories can be retrieved from storage
  * @author Kyle Cranmer
  * @since 0.1
  */
class DefinedCategoriesCanBeRetrievedFromStorage
  extends FreeSpec
    with Matchers {
  "Given storage containing categories can be associated with a book" - {
    AcolyteDriver.register(
      DefinedCategoriesCanBeRetrievedFromStorage.databaseId,
      bookStorageHandler
    )
    val connectionTransactor =
      DriverManagerTransactor[Task](
        "acolyte.jdbc.Driver",
        DefinedCategoriesCanBeRetrievedFromStorage.databaseURL
      )
    val bookStorage: Storage =
      Storage(
        connectionTransactor
      )


    "when storage is requested to retrieve categories that can be associated with a book" - {
      val definedCategoriesFromStorage =
        bookStorage.definedCategories

      "then all categories that can be associated with a book can be retrieved" in {
        definedCategoriesFromStorage shouldEqual DefinedCategoriesCanBeRetrievedFromStorage.definedCategories
      }

    }
  }

  private def bookStorageHandler: StatementHandler =
    AcolyteDSL.handleStatement.withQueryDetection(
      "^SELECT"
    ).withQueryHandler {
      query: QueryExecution =>
        query.sql match {
          case "SELECT Category FROM definedCategories;" =>
            var generatedCategories =
              RowList1AsScala(
                rowList1(
                  classOf[String]
                )
              )
            for (currentCategory <- DefinedCategoriesCanBeRetrievedFromStorage.definedCategories) {
              generatedCategories =
                generatedCategories :+ currentCategory
            }
            generatedCategories.asResult()
        }
  }
}

object DefinedCategoriesCanBeRetrievedFromStorage {
  private val definedCategories =
    Set[Categories](
      "sci-fi",
      "conspiracy",
      "fantasy",
      "thriller"
    )
  private val databaseId: String = "BookStorageTest"
  private  val databaseURL: String =
    "jdbc:acolyte:dcm-tests?handler=" + databaseId
}
