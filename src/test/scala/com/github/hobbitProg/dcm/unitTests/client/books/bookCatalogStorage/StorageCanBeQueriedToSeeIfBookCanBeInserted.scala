package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogStorage

import org.scaltest.FreeSpec

class StorageCanBeQueriedToSeeIfBookCanBeInserted
    extends FreeSpec {
  "Given populated storage to place books into" - {
    "and a title and author of a book that does not exist within the " +
    "storage" - {
      "when the storage is queried to see if the associated book can be " +
      "placed into storage" - {
        "then storage indicates the associated book can be placed into " +
        "storage" in pending
      }
    }

    "and a title and author of a book that already exists within storage" - {
      "when the storage is queried to see if the associated book can be placed into storage" - {
        "then storage indicates the associated book cannot be placed into storage" in pending
      }
    }
  }
}
