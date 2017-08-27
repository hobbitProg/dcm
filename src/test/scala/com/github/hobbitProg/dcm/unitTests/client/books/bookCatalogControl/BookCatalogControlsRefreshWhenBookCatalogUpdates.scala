package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogControl

import java.util.function.{Consumer, Supplier}

import javafx.application.Application
import javafx.stage.Stage

import org.scalatest.{FreeSpec, Matchers}

import org.testfx.api.FxToolkit

import scala.collection.Set

import scalafx.Includes._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.BookCatalog
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository

/**
  * Verifies window that contains book catalog data is updated when
  * associated book catalog is updated
  */
class BookCatalogControlsRefreshWhenBookCatalogUpdates
  extends FreeSpec
    with Matchers {
  "Given a book catalog" - {
    val populatedCatalog: BookCatalog =
      new TestCatalog()

    "and a populated book repository" - {
      val populatedRepository: BookRepository =
        new TestRepository()

      "and a book catalog window" - {
        val testScene: BookCatalogScene =
          createBookCatalogControlScene(
            populatedCatalog,
            populatedRepository
          )

        "and information on a book to place into the book catalog" - {
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
            Some(
              getClass.getResource(
                "/GroundZero.jpg"
              ).toURI
            )
          val newCategories: Set[Categories] =
            Set(
              "sci-fi",
              "conspiracy"
            )

          "when the book is placed into the book catalog" - {
            val updatedCatalog =
              populatedCatalog.add(
                newTitle,
                newAuthor,
                newISBN,
                newDescription,
                newCover,
                newCategories
              )(
                populatedRepository
              )

            "then the new book is displayed on the book catalog control" in {
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

            "and the books originally in the book catalog are still displayed " +
            "on the book catalog control" in {
              testScene.catalogControl.items.value.toSet should contain
              populatedRepository.contents
            }
          }
        }
      }
    }
  }

  "Given a book catalog" - {
    val populatedCatalog: BookCatalog =
      new TestCatalog()

    "and a populated book repository" - {
      val populatedRepository: BookRepository =
        new TestRepository()

      "and a book catalog window" - {
        val testScene: BookCatalogScene =
          createBookCatalogControlScene(
            populatedCatalog,
            populatedRepository
          )

        "and the information on a book within the repository" - {
          val originalBook =
            Book.book(
              "Runs",
              "Kevin J. Anderson",
              "0061052477",
              Some(
                "Description for Ruins"
              ),
              Some(
                getClass.getResource(
                  "/Ruins.jpg"
                ).toURI
              ),
              Set[Categories](
                "sci-fi",
                "conspiracy"
              )
            ) valueOr null

          "and the information on the book with its title changed" - {
            val updatedTitle: Titles =
              "Ruins"

            "when the book is updated within the repository" - {
              populatedCatalog.update(
                originalBook,
                updatedTitle,
                originalBook.author,
                originalBook.isbn,
                originalBook.description,
                originalBook.coverImage,
                originalBook.categories
              ) (
                populatedRepository
              )

              "then the new title is displayed in the book catalog control" in {
                val updatedBooks =
                  testScene.catalogControl.items.value.toSeq.filter {
                    definedBook =>
                    definedBook.title == updatedTitle
                  }
                updatedBooks.length should be (1)
                val updatedBook =
                  updatedBooks(0)
                updatedBook.author should be (originalBook.author)
                updatedBook.isbn should be (originalBook.isbn)
                updatedBook.description should be (originalBook.description)
                updatedBook.coverImage should be (originalBook.coverImage)
                updatedBook.categories should be (originalBook.categories)
              }

              "and the original title is not displayed in the book catalog control" in {
                val originalBooks =
                  testScene.catalogControl.items.value.toSeq.filter {
                    definedBook =>
                    definedBook.title == originalBook.title
                  }
                originalBooks.length should be (0)
              }

              "and the other books in the repository are still displaned on the book catalog control" in {
                testScene.catalogControl.items.value.toSet should contain
                (populatedRepository.contents &~
                  Set(originalBook))
              }
            }
          }
        }
      }
    }
  }

  /**
    * Create scene that contains book catalog control
    * @param catalog Book catalog to use in control
    * @return Scene that contains book catalog control
    */
  private def createBookCatalogControlScene(
    catalog: BookCatalog,
    repository: BookRepository
  ): BookCatalogScene = {
    // Create test application
    FxToolkit.registerPrimaryStage()
    FxToolkit.setupApplication(
      new Supplier[Application] {
        override def get(): BookCatalogControlUnitTestApplication = {
          new BookCatalogControlUnitTestApplication
        }
      }
    )
    FxToolkit.showStage()

    // Create scene that contains book catalog control
    val catalogScene: BookCatalogScene =
      new BookCatalogScene(
        catalog,
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
