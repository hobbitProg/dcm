package com.github.hobbitProg.dcm.client.books.dialog

import scala.collection.Set
import scala.util.{Success, Failure}

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.stage.{FileChooser, Stage}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.BookCatalog
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository

/**
  * Dialog for changing information on book already in catalog
  * @param catalog Catalog to update
  * @param repository Repository to place book catalog into
  * @param coverImageChooser Creates dialog to select cover image for book
  * @param definedCategories Categories that can be associated with book
  * @param originalBook Book from catalog to modify
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyBookDialog(
  private val catalog: BookCatalog,
  private val repository: BookRepository,
  private val coverImageChooser: FileChooser,
  private val definedCategories: Set[Categories],
  private val originalBook: Book
) extends BookEntryDialog(
  catalog,
  repository,
  coverImageChooser,
  definedCategories
) {
  import catalog._

  // Set up book information to be same as original book
  titleControl.text = originalBook.title
  bookBeingEdited.title = originalBook.title

  authorControl.text = originalBook.author
  bookBeingEdited.author = originalBook.author

  isbnControl.text = originalBook.isbn
  bookBeingEdited.isbn = originalBook.isbn

  originalBook.description match {
    case Some(descriptionText) =>
      descriptionControl.text = descriptionText
    case None =>
  }
  bookBeingEdited.description = originalBook.description

  originalBook.coverImage match {
    case Some(imageURI) =>
      coverImageControl.image =
        imageURI
    case None =>
  }
  bookBeingEdited.coverImage = originalBook.coverImage

  categoryControl.items.value.clear()
  categoryControl.items.value.append(
    originalBook.categories.toSeq:_*
  )
  bookBeingEdited.categories = originalBook.categories

  // Update book to catalog when saved
  saveButton.onAction =
    (event: ActionEvent) => {
      val updateResult =
        update(
          originalBook,
          bookBeingEdited.title,
          bookBeingEdited.author,
          bookBeingEdited.isbn,
          bookBeingEdited.description,
          bookBeingEdited.coverImage,
          bookBeingEdited.categories
        ) (
          repository
        )
      updateResult match {
        case Success(_) =>
          val parentStage: Stage =
            window.value.asInstanceOf[javafx.stage.Stage]
          parentStage.close
        case Failure(_) =>
      }
    }

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
    (isbnControl.text.value != originalBook.isbn &&
      userChoseISBNThatExistsInCatalog)
  }
}
