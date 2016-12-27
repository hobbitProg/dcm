package com.github.hobbitProg.dcm.client.linuxDesktop

import scala.collection.Set
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Tab}
import scalafx.scene.layout.AnchorPane
import scalafx.stage.{FileChooser ,Stage}

import com.github.hobbitProg.dcm.client.books.Categories
import com.github.hobbitProg.dcm.client.books.bookCatalog.Catalog
import com.github.hobbitProg.dcm.client.books.dialog.BookEntryDialog

/**
  * Tab containing books in catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookTab(
  private val coverChooser: FileChooser,
  private val catalog: Catalog,
  private val definedCategories: Set[Categories]
) extends Tab {
  // Do not allow tab to control
  closable = false

  // Add button to add book to catalog
  val addButton: Button =
    new Button(
      "+"
    )
  addButton.id =
    BookTab.addButtonId
  //noinspection ScalaUnusedSymbol
  addButton.onAction =
    (event: ActionEvent) => {
      val dialogStage: Stage =
        new Stage
      dialogStage.title =
        BookTab.addBookTitle
      dialogStage.scene =
        new BookEntryDialog(
          coverChooser,
          catalog,
          definedCategories
        )
      dialogStage.showAndWait()
    }
  AnchorPane.setTopAnchor(
    addButton,
    BookTab.addButtonTop
  )
  AnchorPane.setLeftAnchor(
    addButton,
    BookTab.addButtonLeft
  )

  content =
    new AnchorPane {
      children =
        List(
          addButton
        )
    }
}

object BookTab {
  val addButtonId = "AddBookButton"

  val addBookTitle = "Add Book To Catalog"

  private val addButtonTop: Double = 50.0
  private val addButtonLeft: Double = 50.0
}
