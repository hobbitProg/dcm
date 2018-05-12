package com.github.hobbitProg.dcm.client.gui.linuxDesktop

import scala.collection.Set

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Tab}
import scalafx.scene.layout.AnchorPane

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.{Categories,
  BookCatalog}
import BookCatalog._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.books.control.BookDialogParent
import com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.view._
import com.github.hobbitProg.dcm.client.control.BookTabControl
import com.github.hobbitProg.dcm.client.gui.linuxDesktop.dialog.ImageChooser

/**
  * Tab containing books in catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookTab(
  private val coverChooser: ImageChooser,
  var catalog: BookCatalog,
  var repository: BookCatalogRepository,
  private val catalogService: BookCatalogService[BookCatalog],
  private val definedCategories: Set[Categories]
) extends Tab
    with BookDialogParent {
  text = "Books"

  // Load catalog
  catalog =
    load(
      catalog,
      repository.contents
    )

  // Do not allow tab to control
  closable = false

  // Add control to display books within catalog
  val catalogDisplay: BookCatalogView =
    new BookCatalogView(
      catalog
    )
  AnchorPane.setTopAnchor(
    catalogDisplay,
    BookTab.catalogDisplayTop
  )
  AnchorPane.setLeftAnchor(
    catalogDisplay,
    BookTab.catalogDisplayLeft
  )
  catalog =
    catalogDisplay register catalog

  // Add button to add book to catalog
  private val addButton: Button =
    new Button(
      "Add"
    )

  addButton.id =
    BookTab.addButtonId
  //noinspection ScalaUnusedSymbol
  AnchorPane.setTopAnchor(
    addButton,
    BookTab.addButtonTop
  )
  AnchorPane.setLeftAnchor(
    addButton,
    BookTab.addButtonLeft
  )
  addButton.minWidth = BookTab.addButtonMinWidth

  // Add button to modify a book in the catalog
  private val modifyButton: Button =
    new Button(
      "Modify"
    )
  modifyButton.disable = true
  modifyButton.id =
    BookTab.modifyButtonID
  AnchorPane.setTopAnchor(
    modifyButton,
    BookTab.modifyButtonTop
  )
  AnchorPane.setLeftAnchor(
    modifyButton,
    BookTab.modifyButtonLeft
  )

  // Add button to remove book from catalog
  private val deleteButton: Button =
    new Button(
      "Delete"
    )

  deleteButton.disable = true
  deleteButton.id =
    BookTab.deleteButtonID
  AnchorPane.setTopAnchor(
    deleteButton,
    BookTab.deleteButtonTop
  )
  AnchorPane.setLeftAnchor(
    deleteButton,
    BookTab.deleteButtonLeft
  )

  // Add control to display currently added book
  private val selectedBookControl: SelectedBookView =
    new SelectedBookView()
  private val bookControl: BookTabControl =
    new BookTabControl()
  addButton.onAction =
    (event: ActionEvent) => {
      bookControl.addNewBook(
        selectedBookControl,
        catalog,
        repository,
        catalogService,
        this,
        coverChooser,
        definedCategories
      )
    }
  modifyButton.onAction =
    (event: ActionEvent) => {
      bookControl.modifyBook(
        selectedBookControl,
        catalog,
        repository,
        catalogService,
        this,
        coverChooser,
        definedCategories,
        catalogDisplay.selectionModel.value.selectedItem.value
      )
    }
  deleteButton.onAction =
    (event: ActionEvent) => {
      bookControl.deleteBook(
        this,
        catalog,
        repository,
        catalogService,
        catalogDisplay.selectionModel.value.selectedItem.value
      )
    }
  catalogDisplay.selectionModel.value.selectedItem.onChange {
    bookControl.determineModifyButtonActivation(
      modifyButton,
      catalogDisplay.selectionModel.value
    )
    bookControl.determineDeleteButtonActivation(
      deleteButton,
      catalogDisplay.selectionModel.value
    )
  }

  AnchorPane.setTopAnchor(
    selectedBookControl,
    BookTab.selectedBookTop
  )
  AnchorPane.setLeftAnchor(
    selectedBookControl,
    BookTab.selectedBookLeft
  )

  content =
    new AnchorPane {
      children =
        List(
          catalogDisplay,
          addButton,
          modifyButton,
          deleteButton,
          selectedBookControl
        )
    }
}

object BookTab {
  val addButtonId = "AddBookButton"
  val modifyButtonID = "ModifyBookButton"
  val deleteButtonID = "DeleteBookButton"

  private val catalogDisplayTop: Double = 4.0
  private val catalogDisplayLeft: Double = 4.0
  private val addButtonTop: Double = 175.0
  private val addButtonLeft: Double = 255.0
  private val addButtonMinWidth: Double = 62.0
  private val modifyButtonTop: Double = 205.0
  private val modifyButtonLeft: Double = 255.0
  private val deleteButtonTop: Double = 235.0
  private val deleteButtonLeft: Double = 255.0
  private val selectedBookTop: Double = 4.0
  private val selectedBookLeft: Double = 320.0
}
