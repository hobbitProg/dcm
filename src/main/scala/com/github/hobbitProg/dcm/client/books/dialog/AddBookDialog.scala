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
  private val repository: BookRepository,
  private val coverImageChooser: FileChooser,
  private val definedCategories: Set[Categories]
) extends BookEntryDialog(
  catalog,
  repository,
  coverImageChooser,
  definedCategories
) {
  import catalog._

  // Add book to catalog when saved
  saveButton.onAction =
    (event: ActionEvent) => {
      val addResult =
        add(
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
    userChoseISBNThatExistsInCatalog
  }
}
