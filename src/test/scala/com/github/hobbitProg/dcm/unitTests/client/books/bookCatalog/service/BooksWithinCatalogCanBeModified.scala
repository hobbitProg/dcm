package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service

import org.scalatest.FreeSpec

class BooksWithinCatalogCanBeModified
    extends FreeSpec {
  "Given a book catalog" - {
    "and a repository that contains the catalog" - {
      "and a book that is already in the catalog" - {
        "and a book that is the same as the original book except the title" - {
          "and a listener for book modification events" - {
            "when the original book is modified within the catalog" - {
              "then the new book is placed into the catalog" in pending
              "and the new book is given to the listener" in pending
              "and the original book is removed from the catalog" in pending
              "and the original book is given to the listener" in pending
            }
          }
        }
      }
    }
  }
}
