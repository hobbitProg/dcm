package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogWindow

import org.scalatest.FreeSpec

/**
  * Verifies window that contains book catalog data is updated when
  * associated book catalog is updated
  */
class BookCatalogWindowsRefreshWhenBookCatalogUpdates
  extends FreeSpec {
  "Given a book catalog window" - {
    "and given a populated book catalog" - {
      "and a book to place into the book catalog" - {
        "when the book is placed into the book catalog" - {
          "then the new book is displayed on the book catalog window" in
            pending
          "and the books originally in the book catalog are still displayed " +
            "on the book catalog" in
              pending
        }
      }
    }
  }
}
