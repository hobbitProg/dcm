package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogControl

import java.net.URI
import java.util.function.{Consumer, Supplier}
import javafx.application.Application
import javafx.stage.Stage
import org.scalatest.{FreeSpec, Matchers}
import org.testfx.api.FxToolkit
import scala.collection.Set
import scalafx.Includes._

import com.github.hobbitProg.dcm.client.books.bookCatalog.{Book, Catalog}
import com.github.hobbitProg.dcm.client.books.bookCatalog.Implicits._

/**
  * Verifies window that contains book catalog data is updated when
  * associated book catalog is updated
  */
class BookCatalogControlsRefreshWhenBookCatalogUpdatesMultiJvm
  extends FreeSpec
    with Matchers {
  "Given a populated book catalog" - {
    val populatedCatalog: BookCatalogWindowCatalog =
      new BookCatalogWindowCatalog

     "and a book catalog window" - {
       val testScene: BookCatalogScene =
         createBookCatalogControlScene(
           populatedCatalog
         )

      "and a book to place into the book catalog" - {
        val newBook: Book =
          (
            "Ground Zero",
            "Kevin J. Anderson",
            "006105223X",
            Some("Description for Ground Zero"),
            Some[URI](
              getClass.getResource(
                "/GroundZero.jpg"
              ).toURI
            ),
            Set(
              "sci-fi",
              "conspiracy"
            )
          )

        "when the book is placed into the book catalog" - {
          //noinspection ScalaUnusedSymbol
          val updatedCatalog =
            populatedCatalog + newBook

          "then the new book is displayed on the book catalog window" in {
            testScene.catalogControl.items.value.toSet should contain (newBook)
          }

          "and the books originally in the book catalog are still displayed " +
            "on the book catalog" in {
            testScene.catalogControl.items.value.toSet should contain
              populatedCatalog.books
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
    catalog: Catalog
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
