package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import acolyte.jdbc.{AcolyteDSL, StatementHandler, UpdateExecution,
  QueryExecution, RowLists, QueryResult, Driver => AcolyteDriver}
import acolyte.jdbc.Implicits._

import cats._
import cats.data._
import cats.implicits._

import doobie.imports._

import fs2.Task
import fs2.interop.cats._

import java.net.URI

import scala.collection.Set

import com.github.hobbitProg.dcm.client.books._

/**
  * Stub database for verifing storage of catalog
  */
class StubDatabase {
  // ID for acolyte mock database
  private val databaseId: String = "BookStorageTest"

  // URL to connect to acolyte database
  private val databaseURL: String =
    "jdbc:acolyte:dcm-tests?handler=" + databaseId

  // Information on book that already exists within storage
  private val existingTitle : Titles = "Ruins"
  private val existingAuthor: Authors = "Kevin J. Anderson"

  var addedTitle: Titles = ""
  var addedAuthor: Authors = ""
  var addedISBN: ISBNs = ""
  var addedDescription: Descriptions = None
  var addedCover: CoverImageLocations = None
  var addedCategoryAssociations: Set[(ISBNs, Categories)] =
    Set[(ISBNs, Categories)]()

  private var queriedTitle: Titles = ""
  private var queriedAuthor: Authors = ""

  AcolyteDriver.register(
    databaseId,
    bookStorageHandler
  )
  val connectionTransactor =
    DriverManagerTransactor[Task](
      "acolyte.jdbc.Driver",
      databaseURL
    )

  private def bookStorageHandler : StatementHandler =
    AcolyteDSL.handleStatement.withQueryDetection(
      "^SELECT "
    ).withUpdateHandler {
      execution: UpdateExecution =>
        execution.sql match {
          case "INSERT INTO bookCatalog(Title,Author,ISBN,Description,Cover)VALUES(?,?,?,?,?);" =>
            val parameters =
              execution.parameters
            addedTitle =
              parameters.head.value.asInstanceOf[Titles]
            addedAuthor =
              parameters(1).value.asInstanceOf[Authors]
            addedISBN =
              parameters(2).value.asInstanceOf[ISBNs]
            addedDescription =
              Some(
                parameters(3).value.asInstanceOf[String]
              )
            addedCover =
              Some(
                new URI(
                  parameters(4).value.asInstanceOf[String]
                )
              )
          case "INSERT INTO categoryMapping(ISBN,Category)VALUES(?,?);" =>
            val parameters =
              execution.parameters
            val newISBN =
              parameters.head.value.asInstanceOf[ISBNs]
            val newCategory =
              parameters(1).value.asInstanceOf[Categories]
            addedCategoryAssociations =
              addedCategoryAssociations + ((newISBN, newCategory))
        }
        1
    } withQueryHandler {
      query: QueryExecution =>
      query.sql match {
        case "SELECT Title FROM bookCatalog WHERE Title=? AND Author=?;" =>
          val parameters =
            query.parameters
          queriedTitle =
            parameters.head.value.asInstanceOf[Titles]
          queriedAuthor =
            parameters.last.value.asInstanceOf[Authors]
          if (queriedTitle == existingTitle &&
            queriedAuthor == existingAuthor) {
            RowLists.stringList(
              existingTitle
            )
          }
          else {
            QueryResult.Nil
          }
      }
    }
}
