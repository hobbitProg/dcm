package com.github.hobbitProg.dcm.client.control

import scala.collection.Set

import scalafx.scene.control.{Button, MultipleSelectionModel}
import scalafx.stage.Stage

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.{Book,
  BookCatalog, Categories}
import com.github.hobbitProg.dcm.client.books.control.BookDialogParent
import com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.dialog._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.view._
import com.github.hobbitProg.dcm.client.dialog.ImageChooser
/**
  * Control for the tab contianing information within the book catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class BookTabControl {
  def addNewBook(
    selectedBookControl: SelectedBookView,
    catalog: BookCatalog,
    repository: BookCatalogRepository,
    catalogService: BookCatalogService[BookCatalog],
    parent: BookDialogParent,
    coverChooser: ImageChooser,
    definedCategories: Set[Categories]
  ): Unit = {
    selectedBookControl.clear()
    val dialogStage: Stage =
      new Stage
    dialogStage.title =
      BookTabControl.addBookTitle
    dialogStage.scene =
      new AddBookDialog(
        catalog,
        repository,
        catalogService,
        parent,
        coverChooser,
        definedCategories
      )
    dialogStage.showAndWait()
  }

  def determineModifyButtonActivation(
    modifyBookButton: Button,
    catalogViewSelectionModel: MultipleSelectionModel[Book]
  ) = {
    modifyBookButton.disable =
      catalogViewSelectionModel.isEmpty
  }

  def modifyBook(
    selectedBookControl: SelectedBookView,
    catalog: BookCatalog,
    repository: BookCatalogRepository,
    catalogService: BookCatalogService[BookCatalog],
    parent: BookDialogParent,
    coverChooser: ImageChooser,
    definedCategories: Set[Categories],
    originalBook: Book
  ): Unit = {
    selectedBookControl.clear()
    val dialogStage: Stage =
      new Stage
    dialogStage.title =
      BookTabControl.modifyBookTitle
    dialogStage.scene =
      new ModifyBookDialog(
        catalog,
        repository,
        catalogService,
        parent,
        coverChooser,
        definedCategories,
        originalBook
      )
    dialogStage.showAndWait()
  }
}

object BookTabControl {
  val addBookTitle = "Add Book To Catalog"
  val modifyBookTitle = "Modify Book"
}
