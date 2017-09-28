package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import acolyte.jdbc.{AcolyteDSL, StatementHandler, UpdateExecution,
  UpdateResult, Driver => AcolyteDriver}
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

class StubDatabase{
  // ID for acolyte mock database
  private val databaseID: String = "BookCatalogRepositoryTest"

  // URL to connect to acolyte database
  private val databaseURL: String =
    "jdbc:acolyte:dcm-tests?handler=" + databaseID

  var addedTitle: Titles = ""
  var addedAuthor: Authors = ""
  var addedISBN: ISBNs = ""
  var addedDescription: Description = None
  var addedCover: CoverImages = None
  var addedCategoryAssociations: Set[(ISBNs, Categories)] =
    Set[(ISBNs, Categories)]()

  private def bookStorageHandler: StatementHandler =
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
            if (parameters(3).value.asInstanceOf[String] == "NULL") {
              None
            }
            else {
              Some(
                parameters(3).value.asInstanceOf[String]
              )
            }
          addedCover =
            if (parameters(4).value.asInstanceOf[String] == "NULL") {
              None
            }
            else {
              Some(
                new URI(
                  parameters(4).value.asInstanceOf[String]
                )
              )
            }
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
