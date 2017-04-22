package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import org.scalatest.{FreeSpec, Matchers}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.interpreter.DatabaseBookRepositoryInterpreter

class RepositoryCanBeQueriedToSeeIfBookAlreadyExists
    extends FreeSpec
    with Matchers {
  "Given populated storage to place books into" - {
    val database =
      new StubDatabase
    DatabaseBookRepositoryInterpreter.setConnection(
      database.connectionTransactor
    )


    "and a title and author of a book that does not exist within the " +
    "storage" - {
      val title: Titles = "Ground Zero"
      val author: Authors = "Kevin J. Anderson"

      "when the storage is queried to see if the associated book is already " +
      "in storage" - {
        val bookAlreadyExistsInStorage: Boolean =
          DatabaseBookRepositoryInterpreter.alreadyContains (
            title,
            author
          )

        "then storage indicates the associated book is not in storage" in {
          bookAlreadyExistsInStorage shouldEqual false
        }
      }
    }

    "and a title and author of a book that already exists within storage" - {
      val title: Titles = "Ruins"
      val author: Authors = "Kevin J. Anderson"

      "when the storage is queried to see if the associated book already " +
      "exists in storage" - {
        val bookExistsInStorage: Boolean =
          DatabaseBookRepositoryInterpreter.alreadyContains (
            title,
            author
          )

        "then storage indicates the associated book already exists in " +
        "storage" in {
          bookExistsInStorage shouldEqual true
        }
      }
    }
  }
}
