package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogStorage

import acolyte.jdbc.{AcolyteDSL, StatementHandler, QueryExecution, Driver => AcolyteDriver}
import acolyte.jdbc.RowLists._
import acolyte.jdbc.Implicits._

import doobie.imports._

import fs2.Task

import org.scalatest.{FreeSpec, Matchers}

import scala.collection.Set

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.interpreter.DatabaseBookRepositoryInterpreter

/**
  * Verifies books can be retrieved from storage
  * @author Kyle Cranmer
  * @since 0.1
  */
class BooksCanBeRetrievedFromRepository
  extends FreeSpec
    with Matchers {
  "Given populated book storage" - {
    AcolyteDriver.register(
      BooksCanBeRetrievedFromRepository.databaseId,
      bookStorageHandler
    )
    val connectionTransactor =
      DriverManagerTransactor[Task](
        "acolyte.jdbc.Driver",
        BooksCanBeRetrievedFromRepository.databaseURL
      )
    DatabaseBookRepositoryInterpreter setConnection connectionTransactor

    "when books are requested from storage" - {
      val booksFromStorage =
        DatabaseBookRepositoryInterpreter.contents

      "then books are retrieved from storage" in {
        booksFromStorage shouldEqual BooksCanBeRetrievedFromRepository.definedBooks
      }
    }
  }

  private def bookStorageHandler: StatementHandler =
    AcolyteDSL.handleStatement.withQueryDetection(
      "^SELECT"
    ).withQueryHandler {
      query: QueryExecution =>
        query.sql match {
          case "SELECT Title,Author,ISBN,Description,Cover FROM bookCatalog;" =>
            var gatheredBooks =
              RowList5AsScala(
                rowList5(
                  classOf[String],
                  classOf[String],
                  classOf[String],
                  classOf[String],
                  classOf[String]
                )
              )
            for (currentBook <- BooksCanBeRetrievedFromRepository.definedBooks) {
              val definedDescription =
                currentBook.description match {
                  case Some(bookDescription) => bookDescription
                  case None => "NULL"
                }
              val definedCover =
                currentBook.coverImage match {
                  case Some(cover) => cover.toString
                  case None => "NULL"
                }
              gatheredBooks =
                gatheredBooks :+ (
                  currentBook.title,
                  currentBook.author,
                  currentBook.isbn,
                  definedDescription,
                  definedCover
                )
            }
            gatheredBooks.asResult()
          case "SELECT Category FROM categoryMapping WHERE ISBN=?;" =>
            var gatheredCategories =
              RowList1AsScala(
                rowList1(
                  classOf[String]
                )
              )
            val associatedBook =
              BooksCanBeRetrievedFromRepository.definedBooks find {
                book =>
                  book.isbn == query.parameters.head.value
              }
            associatedBook match {
              case Some(matchingBook) =>
                for (matchingCategory <- matchingBook.categories) {
                  gatheredCategories =
                    gatheredCategories :+ matchingCategory
                }
              case None =>
            }
            gatheredCategories.asResult()
        }
    }
}

object BooksCanBeRetrievedFromRepository {
  private class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book {
  }
  private val databaseId: String = "BookStorageTest"
  private val databaseURL: String =
    "jdbc:acolyte:dcm-tests?handler=" + databaseId
  // Contents of book storage
  private val definedBooks: Set[Book] =
    Set[Book](
      Book.book(
        "Ruins",
        "Kevin J. Anderson",
        "0061052477",
        Some(
          "Description for Ruins"
        ),
        Some(
          getClass.getResource(
            "/Ruins.jpg"
          ).toURI
        ),
        Set[Categories](
          "sci-fi",
          "conspiracy"
        )
      ).getOrElse(
        new TestBook(
          "",
          "",
          "",
          None,
          None,
          Set()
        )
      ),
      Book.book(
        "Goblins",
        "Charles Grant",
        "0061054143",
        Some(
          "Description for Goblins"
        ),
        Some(
          getClass.getResource(
            "/Goblins.jpg"
          ).toURI
        ),
        Set[Categories](
          "sci-fi",
          "conspiracy"
        )
      ).getOrElse(
        new TestBook(
          "",
          "",
          "",
          None,
          None,
          Set()
        )
      )
    )
}
