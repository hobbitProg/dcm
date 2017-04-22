package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import acolyte.jdbc.{AcolyteDSL, RowList1, Row1, StatementHandler, QueryExecution, Driver => AcolyteDriver}
import acolyte.jdbc.RowLists.rowList1
import acolyte.jdbc.Implicits._

import doobie.imports._

import fs2.Task

import org.scalatest.{FreeSpec, Matchers}

import scala.collection.Set

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.Categories
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.interpreter.DatabaseBookRepositoryInterpreter

/**
  * Verifies defined book categories can be retrieved from storage
  * @author Kyle Cranmer
  * @since 0.1
  */
class DefinedCategoriesCanBeRetrievedFromRepository
  extends FreeSpec
    with Matchers {
  "Given storage containing categories can be associated with a book" - {
    AcolyteDriver.register(
      DefinedCategoriesCanBeRetrievedFromRepository.databaseId,
      bookStorageHandler
    )
    val connectionTransactor =
      DriverManagerTransactor[Task](
        "acolyte.jdbc.Driver",
        DefinedCategoriesCanBeRetrievedFromRepository.databaseURL
      )

    "when storage is requested to retrieve categories that can be associated with a book" - {
      val definedCategoriesFromStorage =
        DatabaseBookRepositoryInterpreter.definedCategories

      "then all categories that can be associated with a book can be retrieved" in {
        definedCategoriesFromStorage shouldEqual DefinedCategoriesCanBeRetrievedFromRepository.definedCategories
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
            for (currentCategory <- DefinedCategoriesCanBeRetrievedFromRepository.definedCategories) {
              generatedCategories =
                generatedCategories :+ currentCategory
            }
            generatedCategories.asResult()
        }
  }
}

object DefinedCategoriesCanBeRetrievedFromRepository {
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
