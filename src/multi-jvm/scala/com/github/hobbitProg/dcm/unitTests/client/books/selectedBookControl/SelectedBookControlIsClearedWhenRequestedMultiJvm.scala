package com.github.hobbitProg.dcm.unitTests.client.books.selectedBookControl

import java.util.function.{Consumer, Supplier}
import javafx.application.Application
import javafx.stage.Stage
import org.testfx.api.FxToolkit
import org.scalatest.FreeSpec
import scala.collection.Set
import scalafx.Includes._

import com.github.hobbitProg.dcm.client.books.Categories
import com.github.hobbitProg.dcm.client.books.bookCatalog.Book
import com.github.hobbitProg.dcm.client.books.bookCatalog.Implicits._

/**
  * Verifies window containing information on currently selected book is
  * cleared when requested
  */
class SelectedBookControlIsClearedWhenRequestedMultiJvm
  extends FreeSpec {
  "Given a selected book control" - {
    val selectedBookScene =
      createSelectedBookControlScene

    "and a selected book" - {
      val selectedBook: Book =
        (
          "Ruins",
          "Kevin J. Anderson",
          "0061052477",
          "Description for Ruins",
          "Ruins.jpg",
          Set[Categories](
            "sci-fi",
            "conspiracy"
          )
        )
      selectedBookScene.bookControl display selectedBook

      "when the selected book is cleared" - {
        "then there is no title for the selected book" in
          pending

        "and there is no author for the selected book" in
          pending

        "and there is no ISBN for the selected book" in
          pending

        "and there is no description for the selected book" in
          pending

        "and there is no cover image for the selected book" in
          pending

        "and the selected book has no associated categories" in
          pending
      }
    }
  }

  /**
    * Create scene that contains selected book control
    * @return Scene that contains selected book control
    */
  private def createSelectedBookControlScene : SelectedBookScene = {
    // Create test application
    FxToolkit.registerPrimaryStage()
    FxToolkit.setupApplication(
      new Supplier[Application] {
        override def get(): SelectedBookControlUnitTestApplication = {
          new SelectedBookControlUnitTestApplication
        }
      }
    )
    FxToolkit.showStage()

    // Create scene that contains book catalog control
    val bookScene: SelectedBookScene =
      new SelectedBookScene
    FxToolkit.setupStage(
      new Consumer[Stage] {
        override def accept(
          t: Stage
        ): Unit = {
          t.scene =
            bookScene
        }
      }
    )
    FxToolkit.showStage()

    bookScene
  }

}
