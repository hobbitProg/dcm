package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogControl

import java.net.URI
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

            "then the new book is displayed on the book catalog window" in {
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
            "on the book catalog" in {
              testScene.catalogControl.items.value.toSet should contain
              populatedRepository.contents
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
