package com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.dialog

import scala.collection.Set

import scalafx.Includes._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.books.control.{ModifyBookControl,
  BookDialogParent, BookEntryControl}
import com.github.hobbitProg.dcm.client.gui.linuxDesktop.dialog.ImageChooser

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
  import catalogService._

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
    titleIsUndefined ||
  authorIsUndefined ||
  isbnIsUndefined ||
  modifiedTitleAndAuthorAssociatedWithAnotherBook ||
  modifiedISBNAssociatedWithAnotherBook

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

  // Determine if either the title or the author has been modified
  private def titleOrAuthorModified: Boolean =
    titleControl.text.value != originalBook.title ||
  authorControl.text.value != originalBook.author

  // Determine if a book exists with the given title and author
  private def titleAndAuthorAssociatedWithBook: Boolean =
    bookExists(
      catalog,
      titleControl.text.value,
      authorControl.text.value
    )(
      repository
    )

  // Determine if the title and author have been changed to the title and author
  // of another book
  private def modifiedTitleAndAuthorAssociatedWithAnotherBook: Boolean =
    titleOrAuthorModified &&
  titleAndAuthorAssociatedWithBook

  // Determine if the ISBN has been modified
  private def isbnModified: Boolean =
    isbnControl.text.value != originalBook.isbn

  // Determine if a book exists with the given ISBN
  private def isbnAssociatedWithBook: Boolean =
    bookExists(
      catalog,
      isbnControl.text.value
    )(
      repository
    )

  // Determine if the ISBN has been changed to the ISBN of another book
  private def modifiedISBNAssociatedWithAnotherBook: Boolean =
    isbnModified &&
  isbnAssociatedWithBook
}
