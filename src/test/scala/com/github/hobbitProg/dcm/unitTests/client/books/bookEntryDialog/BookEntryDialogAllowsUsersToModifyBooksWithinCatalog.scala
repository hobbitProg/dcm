package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import org.scalatest.FreeSpec

class BookEntryDialogAllowsUsersToModifyBooksWithinCatalog
    extends FreeSpec {
  "Given a book catalog" - {
    "and a populated repository for the book catalog" - {
      "and a book within the repository to modify" - {
        "and a collection of defined categories" - {
          "and dialog to change the details of the given book" - {
            "when the user changes the title of the book to a title not in " +
            "the catalog" - {
              "and the user accpets the updated information" - {
                "then the dialog is closed" in pending
                "and the updated book was added to the catalog" in pending
                "and the original book was removed from the catalog" in pending
              }
            }
          }
        }
      }
    }
  }
}
