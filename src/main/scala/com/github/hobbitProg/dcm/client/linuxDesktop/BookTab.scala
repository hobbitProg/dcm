package com.github.hobbitProg.dcm.client.linuxDesktop

import scala.collection.Set

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Tab}
import scalafx.scene.layout.AnchorPane
import scalafx.stage.{FileChooser ,Stage}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.Categories
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.BookCatalog
import com.github.hobbitProg.dcm.client.books.control._
import com.github.hobbitProg.dcm.client.books.dialog.{AddBookDialog, ModifyBookDialog}

/**
  * Tab containing books in catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookTab(
  private val coverChooser: FileChooser,
  private val catalog: BookCatalog,
  private val repository: BookRepository,
  private val definedCategories: Set[Categories]
) extends Tab {
  text = "Books"

  // Do not allow tab to control
  closable = false

  // Add control to display books within catalog
  val catalogDisplay: BookCatalogControl =
    new BookCatalogControl(
      catalog,
      repository
    )
  AnchorPane.setTopAnchor(
    catalogDisplay,
    BookTab.catalogDisplayTop
  )
  AnchorPane.setLeftAnchor(
    catalogDisplay,
    BookTab.catalogDisplayLeft
  )

  // Add button to add book to catalog
  val addButton: Button =
    new Button(
      "Add"
    )
  addButton.id =
    BookTab.addButtonId
  AnchorPane.setTopAnchor(
    addButton,
    BookTab.addButtonTop
  )
  AnchorPane.setLeftAnchor(
    addButton,
    BookTab.addButtonLeft
  )

  // Add control to display currently selected book
  val selectedBookControl: SelectedBookControl =
    new SelectedBookControl
  addButton.onAction =
    (event: ActionEvent) => {
      selectedBookControl.clear()
      val dialogStage: Stage =
        new Stage
      dialogStage.title =
        BookTab.addBookTitle
      dialogStage.scene =
        new AddBookDialog(
          catalog,
          repository,
          coverChooser,
          definedCategories
        )
      dialogStage.showAndWait()
    }
  AnchorPane.setTopAnchor(
    selectedBookControl,
    BookTab.selectedBookTop
  )
  AnchorPane.setLeftAnchor(
    selectedBookControl,
    BookTab.selectedBookLeft
  )

  // Add control to modify current book
  val modifyButton: Button =
    new Button(
      "Modify"
    )
  modifyButton.id =
    BookTab.modifyButtonId
  AnchorPane.setTopAnchor(
    modifyButton,
    BookTab.modifyButtonTop
  )
  AnchorPane.setLeftAnchor(
    modifyButton,
    BookTab.addButtonLeft
  )
  modifyButton.onAction =
    (event: ActionEvent) => {
      selectedBookControl.clear()
      val dialogStage: Stage =
        new Stage
      dialogStage.title =
        BookTab.modifyBookTitle
      dialogStage.scene =
        new ModifyBookDialog(
          catalog,
          repository,
          coverChooser,
          definedCategories,
          catalogDisplay.selectionModel.value.getSelectedItem
        )
      dialogStage.showAndWait()
    }

  content =
    new AnchorPane {
      children =
        List(
          catalogDisplay,
          addButton,
          modifyButton,
          selectedBookControl
        )
    }
}

object BookTab {
  val addButtonId = "AddBookButton"
  val modifyButtonId = "ModifyBookButton"

  val addBookTitle = "Add Book To Catalog"
  val modifyBookTitle = "Modify Book"

  private val catalogDisplayTop: Double = 4.0
  private val catalogDisplayLeft: Double = 4.0
  private val addButtonTop: Double = 175.0
  private val addButtonLeft: Double = 255.0
  private val modifyButtonTop: Double = 210.0
  private val modifyButtonLeft: Double = 255.0
  private val selectedBookTop: Double = 4.0
  private val selectedBookLeft: Double = 330.0
}
