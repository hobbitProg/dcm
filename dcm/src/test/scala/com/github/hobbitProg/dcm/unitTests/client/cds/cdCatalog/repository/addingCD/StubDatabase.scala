package com.github.hobbitProg.dcm.unitTests.client.cds.cdCatalog.repository.addingCD

import acolyte.jdbc.{AcolyteDSL, StatementHandler, UpdateExecution,
  UpdateResult, QueryExecution, QueryResult, RowLists, Driver => AcolyteDriver}
import acolyte.jdbc.Implicits._

import cats._
import cats.data._
import cats.effect._
import cats.implicits._

import doobie._
import doobie.implicits._

import java.net.URI

import scala.collection.Set

import com.github.hobbitProg.dcm.client.cds.cdCatalog.model._

class StubDatabase {
  // ID for acolyte mock database
  private val databaseID: String = "CDCatalogRepositoryTest"

  // URL to connect to acolyte database
  private val databaseURL: String =
    "jdbc:acolyte:dcm-tests?handler=" + databaseID

  var addedTitle: Titles = ""
  var addedArtist: Artists = ""
  var addedISRC: ISRCs = ""
  var addedCover: CoverImages = None
  var addedCategoryAssociations: Set[(ISRCs, Categories)] =
    Set[(ISRCs, Categories)]()

  private def cdStorageHandler: StatementHandler =
    AcolyteDSL.handleStatement.withUpdateHandler {
      execution: UpdateExecution =>
      execution.sql match {
        case "INSERT INTO cdCatalog(Title,Artist,ISRC,Cover)VALUES(?,?,?,?);" =>
          val parameters =
            execution.parameters
          addedTitle =
            parameters.head.value.asInstanceOf[Titles]
          addedArtist =
            parameters(1).value.asInstanceOf[Artists]
          addedISRC =
            parameters(2).value.asInstanceOf[ISRCs]
          addedCover =
            if (parameters(3).value.asInstanceOf[String] == "NULL") {
              None
            }
            else {
              Some(
                new URI(
                  parameters(3).value.asInstanceOf[String]
                )
              )
            }

        case "INSERT INTO categoryMapping(ISRC,Category)VALUES(?,?);" =>
          val parameters =
            execution.parameters
          val newISRC =
            parameters.head.value.asInstanceOf[ISRCs]
          val newCategory =
            parameters(1).value.asInstanceOf[Categories]
          addedCategoryAssociations =
            addedCategoryAssociations + ((newISRC, newCategory))
      }
      1
    }

  AcolyteDriver.register(
    databaseID,
    cdStorageHandler
  )
  val connectionTransactor =
    Transactor.fromDriverManager[IO](
      "acolyte.jdbc.Driver",
      databaseURL
    )
}
