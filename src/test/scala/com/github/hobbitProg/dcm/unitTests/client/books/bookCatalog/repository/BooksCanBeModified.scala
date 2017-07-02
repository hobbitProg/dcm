package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import org.scalatest.{FreeSpec, Matchers}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.DatabaseBookRepositoryInterpreter

/**
  * Verifies books that exist in repository can be modified
  * @author Kyle Cranmer
  * @since 0.2
  */
class BooksCanBeModified
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

  private val emptyBook =
    TestBook(
      "",
      "",
      "",
      None,
      None,
      Set()
    )

  "Given a populated repostory to modify" - {
    val database =
      new StubDatabase
    DatabaseBookRepositoryInterpreter.setConnection(
      database.connectionTransactor
    )

    "and a book that is contained within the repository" - {
      val originalBook =
        Book.book(
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

      "and another book that is the same as the original book except the title" - {
      val modifiedBook =
        Book.book(
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

        "when the original book is replaced in the repository with the new book" - {
          val updateResult =
            DatabaseBookRepositoryInterpreter.update(
              originalBook getOrElse emptyBook,
              modifiedBook getOrElse emptyBook
            )

          "then the repository is updated" in {
            updateResult shouldBe ('right)
          }

          "and the new book is placed into the repository" in {
            val insertedBook =
              Book.book(
                database.addedTitle,
                database.addedAuthor,
                database.addedISBN,
                database.addedDescription,
                database.addedCover,
                database.addedCategoryAssociations map {
                  categoryAssociation =>
                  categoryAssociation._2
                }
              )
            insertedBook shouldEqual modifiedBook
            (database.addedCategoryAssociations map {
              categoryAssociation =>
              categoryAssociation._1
            }) shouldEqual Set[ISBNs](
              (modifiedBook getOrElse emptyBook).isbn
            )
          }

          "and the original book is removed from the repository" in {
            database.removedISBN shouldBe (originalBook getOrElse emptyBook).isbn
            database.removedCategoryAssociationISBN shouldEqual
              (originalBook getOrElse emptyBook).isbn
          }
        }
      }
    }
  }
}
