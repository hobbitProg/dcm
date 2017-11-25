package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import org.scalatest.FreeSpec

/**
  * Specification for modifying book that exists in catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingBookSpec
    extends FreeSpec {
  "Given the categories that can be associated with books" - {
    "and a book that already exists in the catalog" - {
      "and the new title of the book" - {
        "and the catalog that is being updated" - {
          "and the repository to place book catalog information into" - {
            "and the service for the book catalog" - {
              "and the parent window that created the book modification " +
              "dialog" - {
                "when the book dialog is created" - {
                  "and the title of the book is modified" - {
                    "and the book information is saved" - {
                      "then the book entry dialog is closed" in pending
                      "and the original book is removed via the service" in pending
                      "and the new book is added via the service" in pending
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
