package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogStorage

import org.scalatest.{FreeSpec, Matchers}

import com.github.hobbitProg.dcm.client.books._
import com.github.hobbitProg.dcm.client.books.bookCatalog.storage.Storage

class StorageCanBeQueriedToSeeIfBookCanBeInserted
    extends FreeSpec
    with Matchers {
  "Given populated storage to place books into" - {
    val database =
      new StubDatabase
    val bookStorage: Storage =
      Storage(
        database.connectionTransactor
      )


    "and a title and author of a book that does not exist within the " +
    "storage" - {
      val title: Titles = "Ground Zero"
      val author: Authors = "Kevin J. Anderson"

      "when the storage is queried to see if the associated book can be " +
      "placed into storage" - {
        val bookCanBePlacedIntoStorage: Boolean =
          bookStorage bookCanBePlacedIntoStorage (
            title,
            author
          )

        "then storage indicates the associated book can be placed into " +
        "storage" in {
          bookCanBePlacedIntoStorage shouldEqual true
        }
      }
    }

    "and a title and author of a book that already exists within storage" - {
      val title: Titles = "Ruins"
      val author: Authors = "Kevin J. Anderson"

      "when the storage is queried to see if the associated book can be placed into storage" - {
        val bookCanBePlacedIntoStorage: Boolean =
          bookStorage bookCanBePlacedIntoStorage (
            title,
            author
          )

        "then storage indicates the associated book cannot be placed into storage" in {
          bookCanBePlacedIntoStorage shouldEqual false
        }
      }
    }
  }
}
