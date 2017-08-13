package com.github.hobbitProg.dcm.client.books.dialog

import java.net.URI
import javafx.collections.FXCollections

import scala.collection.Set
import scala.util.{Success, Failure}

import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.AnchorPane
import scalafx.stage.{FileChooser, Stage}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.BookCatalog
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository
import com.github.hobbitProg.dcm.client.books.control.image._
import com.github.hobbitProg.dcm.client.books.control.label._
import com.github.hobbitProg.dcm.client.books.control.listView._
import com.github.hobbitProg.dcm.client.books.control.text._
import com.github.hobbitProg.dcm.client.dialog.CategorySelectionDialog

/**
  * Dialog for entering information on book for catalog
  * @param catalog Catalog to update
  * @param repository Repository to place book catalog into
  * @param coverImageChooser Creates dialog to select cover image for book
  * @param definedCategories Categories that can be associated with book
  * @author Kyle Cranmer
  * @since 0.1
  */

abstract class BookEntryDialog(
  private val catalog: BookCatalog,
  private val repository: BookRepository,
  private val coverImageChooser: FileChooser,
  private val definedCategories: Set[Categories]
)
  extends Scene(
    BookEntryDialog.dialogWidth,
    BookEntryDialog.dialogHeight
) {
  import catalog._

  // Book being edited
  protected val bookBeingEdited: BookModel =
    new BookModel

  // Defined categories that are not associated with book
  val unassociatedCategories: ObservableBuffer[String] =
    new ObservableBuffer[String](
      FXCollections.observableArrayList(
        definedCategories.toList.sortWith(
          (
          leftCategory,
          rightCategory
          ) =>
            leftCategory.compareTo(
              rightCategory
            ) < 0
        ): _*
      )
    )

  // Create control for entering in title of book
  private val titleLabel =
    new TitleLabel
  protected val titleControl: TitleValue =
    new TitleValue
  titleControl.id = BookEntryDialog.titleControlId
  titleControl.text.onChange {
    bookBeingEdited.title = titleControl.text.value
  }

  // Create control for entering in author of book
  private val authorLabel: AuthorLabel =
    new AuthorLabel
  protected val authorControl: AuthorValue =
    new AuthorValue
  authorControl.id = BookEntryDialog.authorControlId
  authorControl.text.onChange {
    bookBeingEdited.author = authorControl.text.value
  }

  // Create control for entering ISBN for book
  private val isbnLabel: ISBNLabel =
    new ISBNLabel
  protected val isbnControl: ISBNValue =
    new ISBNValue
  isbnControl.text.onChange(
    bookBeingEdited.isbn = isbnControl.text.value
  )
  isbnControl.id = BookEntryDialog.isbnControlId

  // Create control for entering book description
  private val descriptionLabel: DescriptionLabel =
    new DescriptionLabel
  protected val descriptionControl: DescriptionValue =
    new DescriptionValue
  descriptionControl.id =
    BookEntryDialog.descriptionControlId
  descriptionControl.text.onChange {
    bookBeingEdited.description =
      Some(
        descriptionControl.text.value
      )
  }

  // Create control to display cover image
  private val coverImageLabel: CoverImageLabel =
    new CoverImageLabel
  protected val coverImageControl: CoverImage =
    new CoverImage

  // Create button to change cover image
  private val coverImageSelectionButton: Button =
    new Button(
      "Change Cover Image"
    )
  coverImageSelectionButton.id =
    BookEntryDialog.bookCoverButtonId
  //noinspection ScalaUnusedSymbol
  coverImageSelectionButton.onAction =
    (event: ActionEvent) => {
      val coverImageFile =
        coverImageChooser.showOpenDialog(
          window.value
        )
      bookBeingEdited.coverImage =
        Some[URI](
          coverImageFile.toURI
        )
      coverImageControl.image =
        coverImageFile.toURI
    }
  AnchorPane.setLeftAnchor(
    coverImageSelectionButton,
    BookEntryDialog.coverImageSelectionLeftBorder
  )
  AnchorPane.setTopAnchor(
    coverImageSelectionButton,
    BookEntryDialog.coverImageSelectionTopBorder
  )

  // Create control to display categories associated with book
  private val categoryLabel: CategoryLabel =
    new CategoryLabel
  protected val categoryControl: BookCategories =
    new BookCategories

  // Create button to update what categories are associated with book
  private val updateCategoriesButton =
    new Button(
      "Update Associated Categories"
    )
  unassociatedCategories onChange {
    updateCategoriesButton.disable =
      unassociatedCategories.length == 0
  }
  //noinspection ScalaUnusedSymbol
  updateCategoriesButton.onAction =
    (event: ActionEvent) => {
      new Stage {
        outer =>
        title = "Associate categories with book"
        scene =
          new CategorySelectionDialog(
            unassociatedCategories,
            categoryControl.categories
          )
      }.showAndWait
      bookBeingEdited.categories =
        categoryControl.categories.toSet[String]
    }
  updateCategoriesButton.id =
    BookEntryDialog.categorySelectionButtonId
  AnchorPane.setTopAnchor(
    updateCategoriesButton,
    BookEntryDialog.categoryUpdateButtonTopBorder
  )
  AnchorPane.setLeftAnchor(
    updateCategoriesButton,
    BookEntryDialog.categoryUpdateButtonLeftBorder
  )

  // Add button to save information
  val saveButton =
    new Button(
      "Save"
    )
  saveButton.id =
    BookEntryDialog.saveButtonId
  saveButton.disable = true
  titleControl.text.onChange {
    saveButton.disable = bookUnableToBeSaved
  }
  authorControl.text.onChange {
    saveButton.disable = bookUnableToBeSaved
  }
  isbnControl.text.onChange {
    saveButton.disable = bookUnableToBeSaved
  }

  AnchorPane.setTopAnchor(
    saveButton,
    BookEntryDialog.saveButtonTopBorder
  )
  AnchorPane.setLeftAnchor(
    saveButton,
    BookEntryDialog.saveButtonLeftButton
  )

  // Set pane for dialog
  content =
    new AnchorPane {
      children =
        List(
          titleLabel,
          titleControl,
          authorLabel,
          authorControl,
          isbnLabel,
          isbnControl,
          descriptionLabel,
          descriptionControl,
          coverImageLabel,
          coverImageControl,
          coverImageSelectionButton,
          categoryLabel,
          categoryControl,
          updateCategoriesButton,
          saveButton
        )
    }

  /**
    * Determines if book cannot be saved
    *
    * @return True if book cannot be saved and false otherwise
    */
  protected def bookUnableToBeSaved: Boolean

  /**
    * Indication if user has not defined title of book
    */
  protected def titleIsUndefined: Boolean =
    titleControl.text.value == ""

  /**
    * Indication if user has defined title of book
    */
  protected def titleIsDefined: Boolean =
    titleControl.text.value != ""

  /**
    * Indication if user has not defined author of book
    */
  protected def authorIsUndefined: Boolean =
    authorControl.text.value == ""

  /**
    * Indication if user has defined author for book
    */
  protected def authorIsDefined: Boolean =
    authorControl.text.value != ""

  /**
    * Indication if user has not defined ISBN for book
    */
  protected def isbnIsUndefined: Boolean =
    isbnControl.text.value == ""

  /**
    * Indication if user has defined ISBN for book
    */
  protected def isbnIsDefined: Boolean =
    isbnControl.text.value != ""

  /**
    * Indication that book with user-defined title by user-defined author
    * already exists in catalog
    */
  protected def titleAuthorPairExistsInCatalog: Boolean =
    existsInCatalog(
      titleControl.text.value,
      authorControl.text.value
    )(
      repository
    )

  /**
    * Indication that book with user-defined ISBN already exists in catalog
    */
  protected def isbnExistsInCatalog: Boolean =
    existsInCatalog(
      isbnControl.text.value
    )(
      repository
    )

  /**
    * Indication that user defined title and author that already exists in
    * catalog
    */
  protected def userChoseTitleAndAuthorThatExistsInCatalog: Boolean =
    titleIsDefined &&
  authorIsDefined &&
  titleAuthorPairExistsInCatalog

  /**
    * Indication that user defined isbn that already exists in catalog
    */
  protected def userChoseISBNThatExistsInCatalog: Boolean =
    isbnIsDefined &&
  isbnExistsInCatalog
}

object BookEntryDialog {
  val titleControlId: String = "bookTitleControl"
  val authorControlId: String = "bookAuthorControl"
  val isbnControlId: String = "bookISBNControl"
  val descriptionControlId: String = "bookDescriptionControl"
  val bookCoverButtonId: String = "bookCoverButton"
  val categorySelectionButtonId: String = "categorySelectionButton"
  val saveButtonId = "saveButton"

  private val coverImageSelectionLeftBorder: Double = 4.0
  private val coverImageSelectionTopBorder: Double = 370.0
  private val categoryUpdateButtonLeftBorder: Double = 185.0
  private val categoryUpdateButtonTopBorder: Double = 370.0
  private val saveButtonLeftButton: Double = 50.0
  private val saveButtonTopBorder: Double = 425.0

  private val dialogWidth: Double = 430.0
  private val dialogHeight: Double = 455.0
}
