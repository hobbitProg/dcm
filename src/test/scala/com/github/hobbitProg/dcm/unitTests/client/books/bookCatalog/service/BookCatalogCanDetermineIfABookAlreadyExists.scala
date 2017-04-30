package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service

import org.scalatest.{FreeSpec, Matchers}

import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.BookCatalogInterpreter

class BookCatalogCanDetermineIfABookAlreadyExists
    extends FreeSpec
    with Matchers {
  "Given a book catalog" - {
    val testCatalog: BookCatalogInterpreter =
      new BookCatalogInterpreter
    import testCatalog._

    "and a repository that contains books" - {
      val bookRepository: BookRepository =
        new FakeRepository()

      "and the title/author pair of a book that does not exist in the catalog" - {
        val title =
          "Ground Zero"
        val author =
          "Kevin J. Anderson"

        "when the catalog is queried to see if a book already exists with the given title and author" - {
          val bookExistsInCatalog: Boolean =
            existsInCatalog(
              title,
              author
            )(
              bookRepository
            )

          "then the catalog indicates there is no book in the catalog that has the given title and author" in {
            bookExistsInCatalog should be (false)
          }
        }
      }
    }
  }

  "Given a book catalog" - {
    val testCatalog: BookCatalogInterpreter =
      new BookCatalogInterpreter
    import testCatalog._

    "and a repository that contains books" - {
      val bookRepository: BookRepository =
        new FakeRepository()

      "and the title/author pair of a book that already exists in the catalog" - {
        val title =
          "Goblins"
        val author =
          "Charles Grant"

        "when the catalog is queried to see if a book already exists with the given title and author" - {
          val bookExistsInCatalog: Boolean =
            existsInCatalog(
              title,
              author
            )(
              bookRepository
            )

          "then the catalog indicates there is a book in the catalog that has the given title and author" in {
            bookExistsInCatalog should be (true)
          }
        }
      }
    }
  }

  "Given a book catalog" - {
    "and a repository that contains books" - {
      "and theISBN of a book that does not exist in the catalog" - {
        "when the catalog is queried to see if a book already exists with " +
        "the given ISBN" - {
          "then the catalog indicates there is no book in the catalog that " -
          "has the given ISBN" is pending
        }
      }
    }
  }

  "Given a book catalog" - {
    "and a repository that contains books" - {
      "and the ISBN of a book that already exists in the catalog" - {
        "when the catalog is queried to see if a book already exists with " +
        "the given ISBN" - {
          "then the catalog indicates there is a book in the catalog that " +
          "has the given ISBN" in pending
        }
      }
    }
  }
}
