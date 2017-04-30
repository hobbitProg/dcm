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
    "repository" - {
      val title: Titles = "Ground Zero"
      val author: Authors = "Kevin J. Anderson"

      "when the reposistory is queried to see if the associated book is " +
      "already in the repository" - {
        val bookAlreadyExistsInStorage: Boolean =
          DatabaseBookRepositoryInterpreter.alreadyContains (
            title,
            author
          )

        "then the repository indicates the associated book is not in the " +
        "repository" in {
          bookAlreadyExistsInStorage shouldEqual false
        }
      }
    }

    "and a title and author of a book that already exists within the " +
    "repository" - {
      val title: Titles = "Ruins"
      val author: Authors = "Kevin J. Anderson"

      "when the repository is queried to see if the associated book already " +
      "exists in the repository" - {
        val bookExistsInStorage: Boolean =
          DatabaseBookRepositoryInterpreter.alreadyContains (
            title,
            author
          )

        "then the repository indicates the associated book already exists in " +
        "the repository" in {
          bookExistsInStorage shouldEqual true
        }
      }
    }

    "and an ISBN of a book that does not exist within the repository" - {
      "when the repository is queried to see if the associated book is " +
      "already in the repository" - {
        "then the repository indicates the associated book is not in the " +
        "repository" in pending
      }
    }

    "and an ISBN of a book that already exists within the repository" - {
      "when the repository is queried to see if the associated book already " +
      "exists in the repository" - {
        "then the repository indicates the associated book already exists in " +
        "the repository" in pending
      }
    }
  }
}
