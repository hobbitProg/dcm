package com.github.hobbitProg.dcm.client.books.dialog

import scala.collection.Set

import scalafx.Includes._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.books.control.{ModifyBookControl,
  BookDialogParent, BookEntryControl}
import com.github.hobbitProg.dcm.client.dialog.ImageChooser

/**
  * Dialog for entering information on book within catalog being modified
  * @param catalog Catalog to update
  * @param repository Repository to place book catalog into
  * @param coverImageChooser Creates dialog to select cover image for book
  * @param definedCategories Categories that can be associated with book
  * @param originalBook Original version of book within catalog
  * @author Kyle Cranmer
  * @since 0.2
*/
class ModifyBookDialog(
  private val catalog: BookCatalog,
  private val repository: BookCatalogRepository,
  private val catalogService: BookCatalogService[BookCatalog],
  private val mainWindow: BookDialogParent,
  private val coverImageChooser: ImageChooser,
  private val definedCategories: Set[Categories],
  private val originalBook: Book
) extends BookEntryDialog(
  catalog,
  repository,
  definedCategories
) {
  // Control for dialog
  protected val control: BookEntryControl =
    new ModifyBookControl(
      catalog,
      coverImageChooser,
      mainWindow,
      catalogService,
      originalBook
    )

  /**
    * Determines if book cannot be saved
    *
    * @return True if book cannot be saved and false otherwise
    */
  protected def bookUnableToBeSaved: Boolean =
    titleIsUndefined

  titleControl.text =
    originalBook.title
  authorControl.text =
    originalBook.author
  isbnControl.text =
    originalBook.isbn
  originalBook.description match {
    case Some(descriptionText) =>
      descriptionControl.text =
        descriptionText
    case None =>
  }

  originalBook.coverImage match {
    case Some(imageLocation) =>
      coverImageControl.image = imageLocation
    case None =>
  }
  categoryControl.items.value ++= originalBook.categories
  unassociatedCategories --= originalBook.categories
}
