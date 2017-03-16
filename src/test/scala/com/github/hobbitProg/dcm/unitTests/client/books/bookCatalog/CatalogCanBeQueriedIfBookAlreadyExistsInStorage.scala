package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog

import org.scalamock.scalatest.MockFactory

import org.scalatest.FreeSpec
import org.scalatest.Matchers

import com.github.hobbitProg.dcm.client.books.bookCatalog.Catalog
//import com.github.hobbitProg.dcm.client.books.bookCatalog.storage.Storage

class CatalogCanBeQueriedIfBookAlreadyExistsInStorage
    extends FreeSpec
    with Matchers
    with MockFactory {
  "Given a populated book catalog" - {
//    val catalogStorage =
//      stub[Storage]
//    (catalogStorage.alreadyContains _).when(
//      "Ground Zero",
//      "Kevin J. Anderson"
//    ).returns(
//      false
//    )
//    val testCatalog =
//      new Catalog(
//        catalogStorage
//      )

    "and the name and author of a book that does not exist within the " +
    "catalog" - {
      val testTitle = "Ground Zero"
      val testAuthor = "Kevin J. Anderson"

      "when the catalog is queried to see if a book with the given title and " +
      "author already exists within the catalog" - {
//        val bookExistsInCatalog =
//          testCatalog.alreadyContains(
//            testTitle,
//            testAuthor
//          )

        "then the catalog indicates the book does not exist within the catalog" in {
//          bookExistsInCatalog shouldEqual false
        }
      }
    }
  }

  "Given a populated book catalog" - {
//    val catalogStorage =
//      stub[Storage]
//    (catalogStorage.alreadyContains _).when(
//      "Ruins",
//      "Kevin J. Anderson"
//    ).returns(
//      true
//    )
//    val testCatalog =
//      new Catalog(
//        catalogStorage
//      )

    "and the name and author of a book that exists within the catalog" - {
      val testTitle = "Ruins"
      val testAuthor = "Kevin J. Anderson"

      "when the catalog is queried to see if a book with the given title " +
      "and author already exists within the catalog" - {
//        val bookExistsInCatalog =
//          testCatalog.alreadyContains(
//            testTitle,
//            testAuthor
//          )

        "then the catalog indicates the book exists within the catalog" in {
//          bookExistsInCatalog shouldEqual true
        }
      }
    }
  }
}
