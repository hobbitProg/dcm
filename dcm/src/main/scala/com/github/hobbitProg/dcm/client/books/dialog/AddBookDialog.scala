package com.github.hobbitProg.dcm.client.books.dialog

import scala.collection.Set

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.stage.Stage

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.books.control.{AddBookControl,
  BookDialogParent, BookEntryControl}
import com.github.hobbitProg.dcm.client.dialog.ImageChooser

/**
  * Dialog for entering information on book to be added to catalog
  * @param catalog Catalog to update
  * @param repository Repository to place book catalog into
  * @param coverImageChooser Creates dialog to select cover image for book
  * @param definedCategories Categories that can be associated with book
  * @author Kyle Cranmer
  * @since 0.2
  */
class AddBookDialog(
  private val catalog: BookCatalog,
  private val repository: BookCatalogRepository,
  private val catalogService: BookCatalogService[BookCatalog],
  private val mainWindow: BookDialogParent,
  private val coverImageChooser: ImageChooser,
  private val definedCategories: Set[Categories]
) extends BookEntryDialog(
  catalog,
  repository,
  definedCategories
) {
  // Control for dialog
  protected def control: BookEntryControl =
    new AddBookControl(
      catalog,
      coverImageChooser,
      mainWindow,
      catalogService
    )

  /**
    * Determines if book cannot be saved
    *
    * @return True if book cannot be saved and false otherwise
    */
  protected def bookUnableToBeSaved: Boolean = {
    titleIsUndefined ||
    authorIsUndefined ||
    isbnIsUndefined ||
    userChoseTitleAndAuthorThatExistsInCatalog ||
    userChoseISBNThatExistsInCatalog
  }
}
