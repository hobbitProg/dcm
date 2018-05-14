package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogView

import java.net.URI
import java.util.function.{Consumer, Supplier}

import javafx.application.Application
import javafx.stage.Stage

import scalafx.Includes._

import org.scalatest.{FreeSpec, Matchers, BeforeAndAfter}

import org.testfx.api.FxToolkit

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository

/**
  *
  */
class UpdateSpec
    extends FreeSpec
    with BeforeAndAfter
    with Matchers {
  private class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book {
  }

  var runningApp: Application = null

  after {
    FxToolkit.cleanupStages()
    FxToolkit.cleanupApplication(
      runningApp
    )
  }

  "Given a book catalog" - {
    val testCatalog: BookCatalog =
      load(
        new BookCatalog(),
        Set[Book](
          new TestBook(
            "Goblins",
            "Charles Grant",
            "0061054143",
            Some(
              "Description for goblins"
            ),
            Some(
              getClass.getResource(
                "/Goblins.jpg"
              ).toURI()
            ),
            Set[Categories](
              "Sci-fi",
              "Conspiracy"
            )
          ),
          new TestBook(
            "Ruins",
            "Kevin J. Anderson",
            "0061057363",
            Some(
              "Description for Ruins"
            ),
            Some(
              getClass.getResource(
                "/Ruins.jpg"
              ).toURI()
            ),
            Set[Categories](
              "Sci-fi",
              "Conspiracy"
            )
          )
        )
      )

    "and a populated book repository" - {
      val testRepository: TestRepository =
        new TestRepository()

      "and a book catalog view" - {
        val testScene: BookCatalogScene =
          createBookCatalogControlScene(
            testCatalog
          )
        val trackedCatalog: BookCatalog =
          testScene.catalogControl register testCatalog

        "and information on a book to add to the book catalog" - {
          val newTitle: Titles =
            "Ground Zero"
          val newAuthor: Authors =
            "Kevin J. Anderson"
          val newISBN: ISBNs =
            "006105223X"
          val newDescription =
            Some(
              "Description for Ground Zero"
            )
          val newCover: CoverImages =
            Some[URI](
              getClass.getResource(
                "/GroundZero.jpg"
              ).toURI
            )
          val newCategories: Set[Categories] =
            Set(
              "sci-fi",
              "conspiracy"
            )

          "when the book is added to the catalog" - {
            val updatedCatalog =
              addBook(
                trackedCatalog,
                newTitle,
                newAuthor,
                newISBN,
                newDescription,
                newCover,
                newCategories
              )

            "then the new book is displayed in the book catalog view" in {
              val newBooks =
                testScene.catalogControl.items.value.toSeq.filter {
                  definedBook =>
                  definedBook.title == newTitle
                }
              newBooks.length should be (1)
              val newBook =
                newBooks(0)
              newBook.author should be (newAuthor)
              newBook.isbn should be (newISBN)
              newBook.description should be (newDescription)
              newBook.coverImage should be (newCover)
              newBook.categories should be (newCategories)
            }

            "and the books that were originally in the book catalog are " +
            "still displayed in the book catalog view" in {
              val originalBooks =
                testScene.catalogControl.items.value.toSeq.filter {
                  definedBook =>
                  definedBook.title != newTitle
                }

              originalBooks.length should be (2)
              originalBooks.toSet should be (testRepository.existingBooks)
            }
          }
        }
      }
    }
  }

  "Given a book catalog" - {
    val testCatalog: BookCatalog =
      load(
        new BookCatalog(),
        Set[Book](
          new TestBook(
            "Goblins",
            "Charles Grant",
            "0061054143",
            Some(
              "Description for goblins"
            ),
            Some(
              getClass.getResource(
                "/Goblins.jpg"
              ).toURI()
            ),
            Set[Categories](
              "Sci-fi",
              "Conspiracy"
            )
          ),
          new TestBook(
            "Ruins",
            "Kevin J. Anderson",
            "0061057363",
            Some(
              "Description for Ruins"
            ),
            Some(
              getClass.getResource(
                "/Ruins.jpg"
              ).toURI()
            ),
            Set[Categories](
              "Sci-fi",
              "Conspiracy"
            )
          )
        )
      )

    "and a populated book repository" - {
      val testRepository: TestRepository =
        new TestRepository()

      "and a book catalog view" - {
        val testScene: BookCatalogScene =
          createBookCatalogControlScene(
            testCatalog
          )
        val trackedCatalog: BookCatalog =
          testScene.catalogControl register testCatalog

        "and the information of the book to modify" - {
          val originalTitle: Titles =
            "Goblins"
          val originalAuthor: Authors =
            "Charles Grant"
          val originalISBN: ISBNs =
            "0061054143"
          val originalDescription: Description =
            Some(
              "Description for goblins"
            )
          val originalCover: CoverImages =
            Some(
              getClass.getResource(
                "/Goblins.jpg"
              ).toURI()
            )
          val originalCategories: Set[Categories] =
            Set[Categories](
              "Sci-fi",
              "Conspiracy"
            )

          "and the new title of the book" - {
            val updatedTitle: Titles =
              "Goblinz"

            "when the book is updated within the catalog" - {
              val originalBook: Book =
                testRepository.existingBooks find {
                  existingBook =>
                  existingBook.title == originalTitle
                } match {
                  case Some(repositoryBook) =>
                    repositoryBook
                  case None =>
                    null
                }

              var updatedCatalog =
                updateBook(
                  trackedCatalog,
                  originalBook,
                  updatedTitle,
                  originalAuthor,
                  originalISBN,
                  originalDescription,
                  originalCover,
                  originalCategories
                )

              "then the original book is not displayed in the book catalog " +
              "view" in {
                val originalVersions =
                  testScene.catalogControl.items.value.toSeq.filter {
                    definedBook =>
                    definedBook.title == originalTitle
                  }
                originalVersions.length should be (0)
                testScene.catalogControl.items.value.toSeq.length should be (2)
              }

              "and the updated book is displayed in the book catalog " +
              "view" in {
                val updatedBooks =
                  testScene.catalogControl.items.value.toSeq.filter {
                    definedBook =>
                    definedBook.title == updatedTitle
                  }
                updatedBooks.length should be (1)
                val updatedBook =
                  updatedBooks(0)
                updatedBook.author should be (originalAuthor)
                updatedBook.isbn should be (originalISBN)
                updatedBook.description should be (originalDescription)
                updatedBook.coverImage should be (originalCover)
                updatedBook.categories should be (originalCategories)
              }

              "and the other books in the catalog are still in the book " +
              "catalog view" in {
                val remainingOriginalBooks =
                  testRepository.existingBooks filter {
                    currentBook =>
                    currentBook.title != originalTitle
                  }
                val displayedOriginalBooks =
                  testScene.catalogControl.items.value.toSeq.filter {
                    currentBook =>
                    currentBook.title != updatedTitle
                  }
                displayedOriginalBooks.toSet should be (remainingOriginalBooks)
              }
            }
          }
        }
      }
    }
  }

  // Create scene that contains book catalog control
  private def createBookCatalogControlScene(
    catalog: BookCatalog
  ): BookCatalogScene = {
    // Create test application
    FxToolkit.registerPrimaryStage()
    runningApp =
      FxToolkit.setupApplication(
        new Supplier[Application] {
          override def get(): BookCatalogViewUnitTestApplication = {
            new BookCatalogViewUnitTestApplication
          }
        }
      )
    FxToolkit.showStage()

    // Create scene that contains book catalog control
    val catalogScene: BookCatalogScene =
      new BookCatalogScene(
        catalog
      )
    FxToolkit.setupStage(
      new Consumer[Stage] {
        override def accept(
          t: Stage
        ): Unit = {
          t.scene =
            catalogScene
        }
      }
    )
    FxToolkit.showStage()

    catalogScene
  }
}
