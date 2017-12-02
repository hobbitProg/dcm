package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogView

import java.net.URI
import java.util.function.{Consumer, Supplier}

import javafx.application.Application
import javafx.stage.Stage

import scalafx.Includes._

import org.scalatest.{FreeSpec, Matchers}

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
    with Matchers {
  "Given a book catalog" - {
    val testCatalog: BookCatalog =
      new BookCatalog()

    "and a populated book repository" - {
      val testRepository: TestRepository =
        new TestRepository()

      "and a book catalog view" - {
        val testScene: BookCatalogScene =
          createBookCatalogControlScene(
            testRepository
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
    "and a populated book repository" - {
      "and a book catalog view" - {
        "and the information of the book to modify" - {
          "and the new title of the book" - {
            "when the book is updated within the catalog" - {
              "then the original book is not displayed in the book catalog " +
              "view" in pending
              "and the updated book is displayed in the book catalog " +
              "view" in pending
              "and the other books in the catalog are still in the book " +
              "catalog view" in pending
            }
          }
        }
      }
    }
  }

  /**
    * Create scene that contains book catalog control
    * @param repository Repository containing book catalog
    * @return Scene that contains book catalog control
    */
  private def createBookCatalogControlScene(
    repository: BookCatalogRepository
  ): BookCatalogScene = {
    // Create test application
    FxToolkit.registerPrimaryStage()
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
        repository
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
