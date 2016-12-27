package com.github.hobbitProg.dcm.client.books.dialog

import java.net.URI
import javafx.collections.FXCollections

import scala.collection.Set
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.AnchorPane
import scalafx.stage.{FileChooser, Stage}

import com.github.hobbitProg.dcm.client.books.Conversions._
import com.github.hobbitProg.dcm.client.books.bookCatalog.{Book, Catalog}
import com.github.hobbitProg.dcm.client.books.control.image._
import com.github.hobbitProg.dcm.client.books.control.label._
import com.github.hobbitProg.dcm.client.books.control.listView._
import com.github.hobbitProg.dcm.client.books.control.text._
import com.github.hobbitProg.dcm.client.dialog.CategorySelectionDialog

/**
  * Dialog for entering information on book for catalog
  * @param coverImageChooser Creates dialog to select cover image for book
  * @param catalog Catalog to update
  * @param definedCategories Categories that can be associated with book
  * @author Kyle Cranmer
  * @since 0.1
  */

class BookEntryDialog(
  private var coverImageChooser: FileChooser,
  private val catalog: Catalog,
  private val definedCategories: Set[String]
)
  extends Scene(
    BookEntryDialog.dialogWidth,
    BookEntryDialog.dialogHeight
  ) {
  // Book being edited
  private val bookBeingEdited: Book =
    new Book

  // Updated book catalog
  var updatedCatalog: Catalog = _

  // Defined categories that are not associated with book
  val unassociatedCategories: ObservableBuffer[String] =
    new ObservableBuffer[String](
      FXCollections.observableArrayList(
        definedCategories.toList.sortWith(
          (leftCategory, rightCategory) =>
            leftCategory.compareTo(
              rightCategory
            ) < 0
        ):_*
      )
    )

  // Create control for entering in title of book
  private val titleLabel =
    new TitleLabel
  private val titleControl: TitleValue =
    new TitleValue
  titleControl.id = BookEntryDialog.titleControlId
  titleControl.text.onChange {
    bookBeingEdited.title = titleControl.text.value
  }

  // Create control for entering in author of book
  private val authorLabel: AuthorLabel =
    new AuthorLabel
  private val authorControl: AuthorValue =
    new AuthorValue
  authorControl.id = BookEntryDialog.authorControlId
  authorControl.text.onChange{
    bookBeingEdited.author = authorControl.text.value
  }

  // Create control for entering ISBN for book
  private val isbnLabel: ISBNLabel =
    new ISBNLabel
  private val isbnControl: ISBNValue =
    new ISBNValue
  isbnControl.text.onChange(
    bookBeingEdited.isbn = isbnControl.text.value
  )
  isbnControl.id = BookEntryDialog.isbnControlId

  // Create control for entering book description
  val descriptionLabel: DescriptionLabel =
    new DescriptionLabel
  val descriptionControl: DescriptionValue =
    new DescriptionValue
  descriptionControl.id =
    BookEntryDialog.descriptionControlId
  descriptionControl.text.onChange {
    bookBeingEdited.description = descriptionControl.text.value
  }

  // Create control to display cover image
  val coverImageLabel: CoverImageLabel =
    new CoverImageLabel
  val coverImageControl: CoverImage =
    new CoverImage

  // Create button to change cover image
  val coverImageSelectionButton: Button =
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
  private val categoryControl: BookCategories =
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
  //noinspection ScalaUnusedSymbol
  saveButton.onAction =
    (event: ActionEvent) => {
      updatedCatalog =
        catalog + bookBeingEdited
      val parentStage: Stage =
        window.value.asInstanceOf[javafx.stage.Stage]
      parentStage.close
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

//  private val dialogWidth: Double = 650.0
  private val dialogWidth: Double = 450.0
  private val dialogHeight: Double = 460.0
}
