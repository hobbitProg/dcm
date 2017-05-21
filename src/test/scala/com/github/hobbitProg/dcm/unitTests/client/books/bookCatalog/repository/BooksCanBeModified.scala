package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import org.scalatest.FreeSpec

class BooksCanBeModified
    extends FreeSpec {
  "Given a repostory to place books into" - {
    "and a book that is contained within the repository" - {
      "and another book that is the same as the original book except the title" - {
        "when the original book is replaced in the repository with the new book" - {
          "then the repository is updated" in pending
          "and the new book is placed into the repository" in pending
          "and the original book is removed from the repository" in pending
        }
      }
    }
  }
}
