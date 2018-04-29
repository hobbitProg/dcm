package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.database

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

  var existingTitle: Titles = _
  var existingAuthor: Authors = _
  var existingISBN: ISBNs = _
  var otherISBN: ISBNs = _

  var removedISBN: ISBNs = _
  var removedCategoryAssociationISBN: ISBNs = _

  private var queriedTitle: Titles = ""
  private var queriedAuthor: Authors = ""
  private var queriedISBN: ISBNs = ""

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
          existingTitle =
            parameters.head.value.asInstanceOf[Titles]
          addedAuthor =
            parameters(1).value.asInstanceOf[Authors]
          existingAuthor =
            parameters(1).value.asInstanceOf[Authors]
          addedISBN =
            parameters(2).value.asInstanceOf[ISBNs]
          existingISBN =
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

          case "DELETE FROM bookCatalog WHERE ISBN=?;"=>
            val parameters =
              execution.parameters
            removedISBN =
              parameters.head.value.asInstanceOf[ISBNs]

          case "DELETE FROM categoryMapping WHERE ISBN=?;" =>
            val parameters =
              execution.parameters
            removedCategoryAssociationISBN =
              parameters.head.value.asInstanceOf[ISBNs]
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
            queriedAuthor == existingAuthor &&
            removedISBN == "") {
            RowLists.stringList(
              existingTitle
            )
          }
          else {
            QueryResult.Nil
          }
        case "SELECT ISBN from bookCatalog where ISBN=?;" =>
          val parameters =
            query.parameters
          queriedISBN =
            parameters.head.value.asInstanceOf[ISBNs]
            if ((queriedISBN == existingISBN &&
              queriedISBN != removedISBN) ||
              queriedISBN == otherISBN) {
            RowLists.stringList(
              existingISBN
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
    Transactor.fromDriverManager[IO](
      "acolyte.jdbc.Driver",
      databaseURL
    )
}
