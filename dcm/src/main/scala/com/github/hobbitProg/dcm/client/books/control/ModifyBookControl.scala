package com.github.hobbitProg.dcm.client.books.control

import cats.data.Validated._

import javafx.stage.Stage

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.{Book,
  BookCatalog}
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.dialog.ImageChooser

/**
  * Control to modify book within catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyBookControl(
  private val catalog: BookCatalog,
  private val coverImageChooser: ImageChooser,
  private val mainWindow: BookDialogParent,
  private val bookCatalogService: BookCatalogService[BookCatalog],
  private val originalBook: Book
) extends BookEntryControl(
  catalog,
  coverImageChooser,
  bookCatalogService
) {
  import bookCatalogService._

  // Set the model to the original information
  bookBeingEdited.title = originalBook.title
  bookBeingEdited.author = originalBook.author
  bookBeingEdited.isbn = originalBook.isbn
  bookBeingEdited.description = originalBook.description
  bookBeingEdited.coverImage = originalBook.coverImage
  bookBeingEdited.categories = originalBook.categories

  /**
    * Save modifications to book
    * @param repository Repository containing book catalog information
    * @param parent Stage containing control
    */
  def saveBook(
    repository: BookCatalogRepository,
    parent: Stage
  ) = {
    val modifyResult =
      modifyBook(
        catalog,
        originalBook,
        bookBeingEdited.title,
        bookBeingEdited.author,
        bookBeingEdited.isbn,
        bookBeingEdited.description,
        bookBeingEdited.coverImage,
        bookBeingEdited.categories
      )(
        repository
      )
    modifyResult match {
      case Valid(updatedCatalog) =>
        parent.close
        mainWindow.catalog = updatedCatalog
      case Invalid(_) =>
    }
  }
}
