package com.github.hobbitProg.dcm.client.linuxDesktop

import scala.collection.Set

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Tab}
import scalafx.scene.layout.AnchorPane
import scalafx.stage.Stage

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.{Categories,
  BookCatalog}
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.books.control.BookDialogParent
import com.github.hobbitProg.dcm.client.books.view._
import com.github.hobbitProg.dcm.client.books.dialog.AddBookDialog
import com.github.hobbitProg.dcm.client.dialog.ImageChooser

/**
  * Tab containing books in catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookTab(
  private val coverChooser: ImageChooser,
  var catalog: BookCatalog,
  private val repository: BookCatalogRepository,
  private val catalogService: BookCatalogService[BookCatalog],
  private val definedCategories: Set[Categories]
) extends Tab
    with BookDialogParent {
  text = "Books"

  // Do not allow tab to control
  closable = false

  // Add control to display books within catalog
  val catalogDisplay: BookCatalogView =
    new BookCatalogView(
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
  catalog =
    catalogDisplay register catalog

  // Add button to add book to catalog
  val addButton: Button =
    new Button(
      "+"
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

  // Add control to display currently added book
  val selectedBookControl: SelectedBookView =
    new SelectedBookView()
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
          catalogService,
          this,
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

  content =
    new AnchorPane {
      children =
        List(
          catalogDisplay,
          addButton,
          selectedBookControl
        )
    }
}

object BookTab {
  val addButtonId = "AddBookButton"

  val addBookTitle = "Add Book To Catalog"

  private val catalogDisplayTop: Double = 4.0
  private val catalogDisplayLeft: Double = 4.0
  private val addButtonTop: Double = 175.0
  private val addButtonLeft: Double = 255.0
  private val selectedBookTop: Double = 4.0
  private val selectedBookLeft: Double = 310.0
}
