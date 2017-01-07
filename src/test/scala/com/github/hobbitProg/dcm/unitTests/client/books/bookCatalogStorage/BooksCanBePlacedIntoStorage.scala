package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogStorage

import acolyte.jdbc.{AcolyteDSL, StatementHandler, UpdateExecution, Driver => AcolyteDriver}
import acolyte.jdbc.Implicits._

import doobie.imports._

import java.net.URI

import org.scalatest.{FreeSpec, Matchers}

import scala.collection.Set

import scalaz.concurrent.Task

import com.github.hobbitProg.dcm.client.books._
import com.github.hobbitProg.dcm.client.books.bookCatalog.Book
import com.github.hobbitProg.dcm.client.books.bookCatalog.Implicits._
import com.github.hobbitProg.dcm.client.books.bookCatalog.storage.Storage

/**
  * Verifies books can be placed into storage
  * @author Kyle Cranmer
  * @since 0.1
  */
class BooksCanBePlacedIntoStorage
  extends FreeSpec
    with Matchers {
  // ID for acolyte mock database
  private val databaseId: String = "BookStorageTest"

  // URL to connect to acolyte database
  private  val databaseURL: String =
    "jdbc:acolyte:dcm-tests?handler=" + databaseId

  private var addedTitle: Titles = ""
  private var addedAuthor: Authors = ""
  private var addedISBN: ISBNs = ""
  private var addedDescription: Descriptions = None
  private var addedCover: CoverImageLocations = None
  private var addedCategoryAssociations: Set[(ISBNs, Categories)] =
    Set[(ISBNs, Categories)]()

  "Given storage to place books into" - {
    addedTitle = ""
    addedAuthor = ""
    addedISBN = ""
    addedDescription = None
    addedCover = None
    addedCategoryAssociations =
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
    val bookStorage: Storage =
      Storage(
        connectionTransactor
      )

    "and a book containing all required information to place into storage" - {
      val bookToStore: Book =
        (
          "Ground Zero",
          "Kevin J. Anderson",
          "006105223X",
          Some(
            "Description for Ground Zero"
          ),
          Some(
            getClass.getResource(
              "/GroundZero.jpg"
            ).toURI
          ),
          Set[Categories](
            "sci-fi",
            "conspiracy"
          )
        )

      "when the book is placed into storage" - {
        val updatedStorage =
          bookStorage save bookToStore

        "then storage is updated" in {
          updatedStorage shouldBe defined
        }

        "and the book is placed into storage" in {
          val enteredBook: Book = (
            addedTitle,
            addedAuthor,
            addedISBN,
            addedDescription,
            addedCover,
            addedCategoryAssociations map {
              categoryAssociation =>
                categoryAssociation._2
            }
          )
          enteredBook shouldEqual bookToStore
          (addedCategoryAssociations map {
            categoryAssociation =>
              categoryAssociation._1
          }) shouldEqual Set[ISBNs](bookToStore.isbn)
        }
      }
    }

    "and a book without a title to place into storage" - {
      val bookToStore: Book =
        (
          "",
          "Kevin J. Anderson",
          "006105223X",
          Some(
            "Description for Ground Zero"
          ),
          Some(
            getClass.getResource(
              "/GroundZero.jpg"
            ).toURI
          ),
          Set[Categories](
            "sci-fi",
            "conspiracy"
          )
        )

      "when the book is placed into storage" - {
        "then the book is not placed into storage" in pending
      }
    }
  }

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
