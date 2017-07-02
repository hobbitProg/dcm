package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service

import org.scalatest.{FreeSpec, Matchers}

import scala.util.Success

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.
  BookCatalogInterpreter

class BooksWithinCatalogCanBeModified
    extends FreeSpec
    with Matchers {
  private case class TestBook(
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    coverImage: CoverImages,
    categories: Set[Categories]
  ) extends Book {
  }

  "Given a book catalog" - {
    val testCatalog: BookCatalogInterpreter =
      new BookCatalogInterpreter

    "and a repository that contains the catalog" - {
      val testRepository =
        new FakeRepository

      "and a book that is already in the catalog" - {
        val originalBook =
          TestBook(
            "Ruins",
            "Charles Grant",
            "0061054143",
            Some(
              "Description for Goblins"
            ),
            Some(
              getClass.getResource(
                "/Goblins.jpg"
              ).toURI
            ),
            Set[Categories](
              "sci-fi",
              "conspiracy"
            )
          )

        "and a book that is the same as the original book except the title" - {
          val updatedBook =
            TestBook(
              "Goblins",
              "Charles Grant",
              "0061054143",
              Some(
                "Description for Goblins"
              ),
              Some(
                getClass.getResource(
                  "/Goblins.jpg"
                ).toURI
              ),
              Set[Categories](
                "sci-fi",
                "conspiracy"
              )
            )

          "and a listener for book modification events" - {
            var bookBeforeUpdate: Book = null
            var bookAfterUpdate: Book = null
            testCatalog.onModify(
              (originalBook, updatedBook) => {
                bookBeforeUpdate = originalBook
                bookAfterUpdate = updatedBook
              }
            )

            "when the original book is modified within the catalog" - {
              val updatedBookInCatalog =
                testCatalog.update(
                  originalBook,
                  updatedBook.title,
                  updatedBook.author,
                  updatedBook.isbn,
                  updatedBook.description,
                  updatedBook.coverImage,
                  updatedBook.categories
                )(
                  testRepository
                )

              "then the new book is placed into the catalog" in {
                updatedBookInCatalog should be (an[Success[_]])
                updatedBookInCatalog.get should be (updatedBook)
                testRepository.savedBook should be (updatedBook)
              }

              "and the updated book is given to the listener" in {
                bookAfterUpdate should be (updatedBook)
              }

              "and the original book is removed from the catalog" in {
                testRepository.removedBook should be (originalBook)
              }

              "and the original book is given to the listener" in {
                bookBeforeUpdate should be (originalBook)
              }
            }
          }
        }
      }
    }
  }
}
