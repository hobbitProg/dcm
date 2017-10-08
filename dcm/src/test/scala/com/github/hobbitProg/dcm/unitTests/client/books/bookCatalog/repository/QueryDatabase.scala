package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import acolyte.jdbc.{AcolyteDSL, StatementHandler, UpdateExecution,
  UpdateResult, QueryExecution, QueryResult, RowLists, Driver => AcolyteDriver}
import acolyte.jdbc.Implicits._

import cats._
import cats.data._
import cats.implicits._

import doobie.imports._

import fs2.Task
import fs2.interop.cats._

import java.net.URI

import scala.collection.Set

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * Stub database for determining if book exists in repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class QueryDatabase {
  type TitlesAuthors = (Titles, Authors)

  // ID for acolyte mock database
  private val databaseID: String = "BookCatalogRepositoryQueryTest"

  // URL to connect to acolyte database
  private val databaseURL: String =
    "jdbc:acolyte:dcm-tests?handler=" + databaseID

  // Books with given title/author within database
  private var availableTitlesAuthors:Set[TitlesAuthors] =
    Set[TitlesAuthors]()

  // Books with given ISBN within database
  private var availableISBNs: Set[ISBNs] =
    Set[ISBNs]()

  private def bookStorageHandler: StatementHandler =
    AcolyteDSL.handleStatement.withQueryDetection(
      "^SELECT "
    ).withUpdateHandler {
      execution: UpdateExecution =>
      execution.sql match {
        case "INSERT INTO bookCatalog(Title,Author,ISBN,Description,Cover)VALUES(?,?,?,?,?);" =>
          val parameters =
            execution.parameters
          availableTitlesAuthors =
            availableTitlesAuthors +
          Tuple2(parameters.head.value.asInstanceOf[Titles],
            parameters(1).value.asInstanceOf[Authors])
        case "INSERT INTO categoryMapping(ISBN,Category)VALUES(?,?);" =>
          val parameters =
            execution.parameters
          val newISBN =
            parameters.head.value.asInstanceOf[ISBNs]
          availableISBNs =
            availableISBNs + newISBN
      }
      1
    } withQueryHandler {
      query: QueryExecution =>
      query.sql match {
        case "SELECT Title FROM bookCatalog WHERE Title=? AND Author=?;" =>
          val parameters =
            query.parameters
          val queriedTitle: Titles =
            parameters.head.value.asInstanceOf[Titles]
          val queriedAuthor: Authors =
            parameters.last.value.asInstanceOf[Authors]
          if (availableTitlesAuthors contains (queriedTitle, queriedAuthor)) {
            RowLists.stringList(
              queriedTitle
            )
          }
          else {
            QueryResult.Nil
          }
        case "SELECT ISBN from bookCatalog where ISBN=?;" =>
          val parameters =
            query.parameters
          val queriedISBN: ISBNs =
            parameters.head.value.asInstanceOf[ISBNs]
          if (availableISBNs contains queriedISBN) {
            RowLists.stringList(
              queriedISBN
            )
          }
          else {
            QueryResult.Nil
          }
      }
    }

  AcolyteDriver.register(
    databaseID,
    bookStorageHandler
  )

  val connectionTransactor =
    DriverManagerTransactor[Task](
      "acolyte.jdbc.Driver",
      databaseURL
    )
}
