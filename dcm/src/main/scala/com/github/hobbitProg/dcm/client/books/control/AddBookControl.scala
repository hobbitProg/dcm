package com.github.hobbitProg.dcm.client.books.control

import cats.data.Validated._

import javafx.stage.Stage

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.BookCatalog
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.gui.linuxDesktop.dialog.ImageChooser

/**
  * Control to add book to catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class AddBookControl(
  private val catalog: BookCatalog,
  private val coverImageChooser: ImageChooser,
  private val mainWindow: BookDialogParent,
  private val bookCatalogService: BookCatalogService[BookCatalog]
) extends BookEntryControl(
  catalog,
  coverImageChooser,
  bookCatalogService
) {
  import bookCatalogService._

  /**
    * Save modifications to book
    * @param repository Repository containing book catalog information
    * @param parent Stage containing control
    */
  def saveBook(
    repository: BookCatalogRepository,
    parent: Stage
  ) = {
    val addResult =
      insertBook(
        catalog,
        bookBeingEdited.title,
        bookBeingEdited.author,
        bookBeingEdited.isbn,
        bookBeingEdited.description,
        bookBeingEdited.coverImage,
        bookBeingEdited.categories
      )(
        repository
      )
    addResult match {
      case Valid(updatedCatalog) =>
        parent.close
        mainWindow.catalog = updatedCatalog
      case Invalid(_) =>
    }
  }
}
