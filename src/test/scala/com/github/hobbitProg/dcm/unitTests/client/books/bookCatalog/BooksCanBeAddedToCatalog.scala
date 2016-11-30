package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog

import acolyte.jdbc.{AcolyteDSL, StatementHandler, UpdateExecution, Driver => AcolyteDriver}
import acolyte.jdbc.Implicits._
import java.sql.{Connection, DriverManager}

import org.scalatest.FreeSpec
import org.scalatest.Matchers._

import scala.collection.Set
import scala.util.matching.Regex
import com.github.hobbitProg.dcm.client.books.{Authors, Categories,
CoverImageLocations, Descriptions, ISBNs, Titles}
import com.github.hobbitProg.dcm.client.books.bookCatalog.{Book, Catalog}
import com.github.hobbitProg.dcm.client.books.bookCatalog.Implicits._

/**
  * Verifies books can be added to catalog
  */
class BooksCanBeAddedToCatalog
  extends FreeSpec {
  // ID for acolyte mock database
  private val databaseId: String = "CatalogAddTest"

  // URL to connect to acolyte database
  private  val databaseURL: String =
  "jdbc:acolyte:dcm-tests?handler=" + databaseId

  // Regular expression to match SQL statement to add book to database
  private val bookAddSQL: Regex =
    ("INSERT INTO bookCatalog[(]Title,Author,ISBN,Description,Cover[)]VALUES[" +
      "(]'([^']+)','([^']+)','([^']+)','([^']+)','([^']+)'[)]").r

  // Regular expression to match SQL statement to associate category with book
  private val categoryAddSQL: Regex =
  ("INSERT INTO catetegoryMapping [(]ISBN,Category[)]VALUES[(]'([^']+)','" +
      "([^']+)'[)]").r

  private var addedTitle: Titles = ""
  private var addedAuthor: Authors = ""
  private var addedISBN: ISBNs = ""
  private var addedDescription: Descriptions = ""
  private var addedCover: CoverImageLocations = ""
  private var addedCategoryAssociations: Set[(ISBNs, Categories)] =
    Set[(ISBNs, Categories)]()

  "Given a book catalog" - {
    addedTitle = ""
    addedAuthor = ""
    addedISBN = ""
    addedDescription = ""
    addedDescription = ""
    addedCover = ""
    addedCategoryAssociations =
      Set[(String, String)]()
    AcolyteDriver.register(
      databaseId,
      BookCatalogHandler
    )
    val bookCatalogConnection: Connection =
      DriverManager.getConnection(
        databaseURL
      )
    val originalBookCatalog: Catalog =
      Catalog(
        bookCatalogConnection
      )

    "and a listener for book addition events" - {
      var bookThatWasBroadcast: Book =
        null
      originalBookCatalog onAdd {
        addedBook =>
          bookThatWasBroadcast = addedBook
      }

      "and a book to add to the catalog" - {
        val newBook: Book =
          (
            "Ground Zero",
            "Kevin J. Anderson",
            "006105223X",
            "Description for Ground Zero",
            "GrouondZero.jpg",
            Set[String](
              "sci-fi",
              "conspiracy"
            )
            )

        "when the book is added to the catalog" - {
          val updatedBookCatalog =
            originalBookCatalog + newBook

          "then the book is added to the catalog" in {
            val enteredBook: Book =
              (
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
            enteredBook shouldEqual newBook
            (addedCategoryAssociations map {
              categoryAssociation =>
                categoryAssociation._1
            }) shouldEqual Set[ISBNs](newBook.isbn)
          }

          "and the book is given to the listener" in {
            bookThatWasBroadcast shouldEqual newBook
          }
        }
      }
    }
  }

  private def BookCatalogHandler : StatementHandler =
    AcolyteDSL.handleStatement.withUpdateHandler {
      execution: UpdateExecution =>
        execution.sql match {
          case bookAddSQL(
          newTitle,
          newAuthor,
          newISBN,
          newDescription,
          newCover
          ) =>
            addedTitle = newTitle
            addedAuthor = newAuthor
            addedISBN = newISBN
            addedDescription = newDescription
            addedCover = newCover
          case categoryAddSQL(
          newISBN,
          newCategory
          ) =>
            addedCategoryAssociations =
              addedCategoryAssociations + ((newISBN, newCategory))
        }
        1
    }
}
