package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogStorage

import acolyte.jdbc.{AcolyteDSL, StatementHandler, UpdateExecution, Driver => AcolyteDriver}
import acolyte.jdbc.Implicits._

import doobie.imports._

import java.net.URI

import scala.collection.Set

import scalaz.concurrent.Task

import com.github.hobbitProg.dcm.client.books._

/**
  * Stub database for verifing storage of catalog
  */
class StubDatabase {
  // ID for acolyte mock database
  private val databaseId: String = "BookStorageTest"

  // URL to connect to acolyte database
  private  val databaseURL: String =
    "jdbc:acolyte:dcm-tests?handler=" + databaseId

  var addedTitle: Titles = ""
  var addedAuthor: Authors = ""
  var addedISBN: ISBNs = ""
  var addedDescription: Descriptions = None
  var addedCover: CoverImageLocations = None
  var addedCategoryAssociations: Set[(ISBNs, Categories)] =
    Set[(ISBNs, Categories)]()

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
    AcolyteDSL.handleStatement.withUpdateHandler {
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
    }
}
