package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog

import org.scalatest.FreeSpec

/**
  * Verifies books can be added to catalog
  */
class BooksCanBeAddedToCatalog
  extends FreeSpec {
  "Given a populated book catalog" - {
    "and a listener for book addition events" - {
      "and a book to add to the catalog" - {
        "when the book is added to the catalog" - {
          "then the book is added to the catalog" in pending
          "and the book is given to the listener" in pending
        }
      }
    }
  }
}
