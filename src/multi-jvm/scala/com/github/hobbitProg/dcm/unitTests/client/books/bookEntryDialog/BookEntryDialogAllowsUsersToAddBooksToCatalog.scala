package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import org.scalatest.FreeSpec

/**
  * Verifies dialog that allows books to be edited can add books to catalog
  */
class BookEntryDialogAllowsUsersToAddBooksToCatalog
  extends FreeSpec {
  "Given a populated book catalog" - {
    "and a collection of defined categories" - {
      "and dialog to fill with details of book to add to catalog" - {
        "when the user enters the title of the new book" - {
          "and the user enters the author of the new book" - {
            "and the user enters the ISBN of the new book" - {
              "and the user enters the description of the new book" - {
                "and the user selects the cover image for the new book" - {
                  "and the user requests to associate categories with the new" +
                    " book" - {
                    "and the user selects the first category with the new " +
                      "book" - {
                      "and the user selects the second category with the new " +
                        "book" - {
                        "when the user accepts the information on the new " +
                          "book" - {
                          "then the dialog is closed" in
                            pending
                          "and the book was added to the catalog" in
                            pending
                          "and the original contents of the catalog still " +
                            "exist in the catalog" in
                            pending
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
