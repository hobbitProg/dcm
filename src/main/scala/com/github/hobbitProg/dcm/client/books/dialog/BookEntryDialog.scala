package com.github.hobbitProg.dcm.client.books.dialog

import java.io.FileInputStream

import javafx.collections.FXCollections
import javafx.scene.layout.{Border, BorderStroke, BorderStrokeStyle,
CornerRadii}
import javafx.scene.paint.Color
import scala.collection.Set
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, ListView, TextArea, TextField}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{AnchorPane, VBox}
import scalafx.stage.{FileChooser, Stage}

import com.github.hobbitProg.dcm.client.control.DisableSelectionModel
import com.github.hobbitProg.dcm.client.books.bookCatalog.{Book, Catalog}
import com.github.hobbitProg.dcm.client.dialog.CategorySelectionDialog
/**
  * Dialog for entering information on book for catalog
  * @param coverImageChooser Creates dialog to select cover image for book
  * @param catalog Catalog to update
  * @param definedCategories Categories that can be associtated with book
  * @author Kyle Cranmer
  * @since 0.1
  */

class BookEntryDialog(
  private val coverImageChooser: FileChooser,
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
    new Label(
      "Title:"
    )
  AnchorPane.setTopAnchor(
    titleLabel,
    BookEntryDialog.titleTopBorder
  )
  AnchorPane.setLeftAnchor(
    titleLabel,
    BookEntryDialog.labelLeftBorder
  )

  private val titleControl: TextField =
    new TextField
  titleControl.id = BookEntryDialog.titleControlId
  titleControl.text.onChange {
    bookBeingEdited.title = titleControl.text.value
  }
  AnchorPane.setTopAnchor(
    titleControl,
    BookEntryDialog.titleTopBorder
  )
  AnchorPane.setLeftAnchor(
    titleControl,
    BookEntryDialog.textFieldLeftBorder
  )

  // Create control for entering in author of book
  private val authorLabel: Label =
    new Label(
      "Label:"
    )
  AnchorPane.setTopAnchor(
    authorLabel,
    BookEntryDialog.authorTopBorder
  )
  AnchorPane.setLeftAnchor(
    authorLabel,
    BookEntryDialog.labelLeftBorder
  )

  private val authorControl: TextField =
    new TextField
  authorControl.id = BookEntryDialog.authorControlId
  authorControl.text.onChange{
    bookBeingEdited.author = authorControl.text.value
  }
  AnchorPane.setTopAnchor(
    authorControl,
    BookEntryDialog.authorTopBorder
  )
  AnchorPane.setLeftAnchor(
    authorControl,
    BookEntryDialog.textFieldLeftBorder
  )

  // Create control for entering ISBN for book
  private val isbnLabel: Label =
    new Label(
      "ISBN:"
    )
  AnchorPane.setTopAnchor(
    isbnLabel,
    BookEntryDialog.isbnTopBorder
  )
  AnchorPane.setLeftAnchor(
    isbnLabel,
    BookEntryDialog.labelLeftBorder
  )

  val isbnControl: TextField =
    new TextField
  isbnControl.text.onChange(
    bookBeingEdited.isbn = isbnControl.text.value
  )
  isbnControl.id = BookEntryDialog.isbnControlId
  AnchorPane.setTopAnchor(
    isbnControl,
    BookEntryDialog.isbnTopBorder
  )
  AnchorPane.setLeftAnchor(
    isbnControl,
    BookEntryDialog.textFieldLeftBorder
  )

  // Create control for entering book description
  val descriptionLabel: Label =
    new Label(
      "Description:"
    )
  AnchorPane.setTopAnchor(
    descriptionLabel,
    BookEntryDialog.descriptionLabelTopBorder
  )
  AnchorPane.setLeftAnchor(
    descriptionLabel,
    BookEntryDialog.labelLeftBorder
  )

  val descriptionControl: TextArea =
    new TextArea
  descriptionControl.id =
    BookEntryDialog.descriptionControlId
  descriptionControl.text.onChange {
    bookBeingEdited.description = descriptionControl.text.value
  }
  AnchorPane.setTopAnchor(
    descriptionControl,
    BookEntryDialog.descrptionControlTopBorder
  )
  AnchorPane.setLeftAnchor(
    descriptionControl,
    BookEntryDialog.descriptionControlLeftBorder
  )

  // Create control to display cover image
  val coverImageLabel: Label =
    new Label(
      "Cover Image:"
    )
  AnchorPane.setLeftAnchor(
    coverImageLabel,
    BookEntryDialog.coverImageLabelLeftBorder
  )
  AnchorPane.setTopAnchor(
    coverImageLabel,
    BookEntryDialog.coverImageLabelTopBorder
  )
  val coverImageControl: ImageView =
    new ImageView
  val coverImagePane: VBox =
    new VBox {
      border =
        new Border(
          new BorderStroke(
            Color.BLACK,
            BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY,
            BorderStroke.THIN
          )
        )
      children =
        List(
          coverImageControl
        )
      minHeight = BookEntryDialog.coverImageHeight
      minWidth = BookEntryDialog.coverImageWidth
    }
  AnchorPane.setLeftAnchor(
    coverImagePane,
    BookEntryDialog.coverImageLeftBorder
  )
  AnchorPane.setTopAnchor(
    coverImagePane,
    BookEntryDialog.coverImageTopBorder
  )

  // Create button to change cover image
  val coverImageSelectionButton: Button =
    new Button(
      "Change Cover Image"
    )
  coverImageSelectionButton.id =
    BookEntryDialog.bookCoverButtonId
  coverImageSelectionButton.onAction =
    (event: ActionEvent) => {
      val coverImageFile =
        coverImageChooser.showOpenDialog(
          window.value
        )
      bookBeingEdited.coverImage =
        coverImageFile.getName
      coverImageControl.image =
        new Image(
          new FileInputStream(
            coverImageFile
          ),
          BookEntryDialog.coverImageWidth,
          BookEntryDialog.coverImageHeight,
          true,
          true
        )
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
  private val categoryLabel: Label =
    new Label(
      "Associated Categories:"
    )
  AnchorPane.setTopAnchor(
    categoryLabel,
    BookEntryDialog.categoryLabelTopBorder
  )
  AnchorPane.setLeftAnchor(
    categoryLabel,
    BookEntryDialog.categoryControlLeftBorder
  )
  private val associatedCategories: ObservableBuffer[String] =
    new ObservableBuffer[String](
      FXCollections.observableArrayList()
    )
  private val categoryControl: ListView[String] =
    new ListView[String](
      associatedCategories
    )
  categoryControl.selectionModel =
    new DisableSelectionModel[String]
  AnchorPane.setTopAnchor(
    categoryControl,
    BookEntryDialog.categoryControlTopBorder
  )
  AnchorPane.setLeftAnchor(
    categoryControl,
    BookEntryDialog.categoryControlLeftBorder
  )

  // Create button to update what categories are associated with book
  private val updateCategoriesButton =
    new Button(
      "Update Associated Categories"
    )
  unassociatedCategories onChange {
    updateCategoriesButton.disable =
      unassociatedCategories.length == 0
  }
  updateCategoriesButton.onAction =
    (event: ActionEvent) => {
      new Stage {
        outer =>
        title = "Associate categories with book"
        scene =
          new CategorySelectionDialog(
            unassociatedCategories,
            associatedCategories
          )
      }.showAndWait
      bookBeingEdited.categories =
        associatedCategories.toSet[String]
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
          coverImagePane,
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

  private val titleTopBorder: Double = 2.0
  private val authorTopBorder: Double = 30.0
  private val isbnTopBorder: Double = 58.0
  private val descriptionLabelTopBorder: Double = 86.0
  private val descriptionControlLeftBorder: Double = 2.0
  private val descrptionControlTopBorder: Double = 114.0
  private val labelLeftBorder: Double = 2.0
  private val textFieldLeftBorder: Double = 90.0
  private val coverImageLabelLeftBorder: Double = 2.0
  private val coverImageLabelTopBorder: Double = 296.0
  private val coverImageLeftBorder: Double = 2.0
  private val coverImageTopBorder: Double = 324.0
  private val coverImageSelectionLeftBorder: Double = 75.0
  private val coverImageSelectionTopBorder: Double = 725.0
  private val categoryLabelLeftBorder: Double = 310.0
  private val categoryLabelTopBorder: Double = 296.0
  private val categoryControlLeftBorder: Double = 310.0
  private val categoryControlTopBorder: Double = 324.0
  private val categoryUpdateButtonLeftBorder: Double = 325.0
  private val categoryUpdateButtonTopBorder: Double = 725.0
  private val saveButtonLeftButton: Double = 325.0
  private val saveButtonTopBorder: Double = 800.0

  private val dialogWidth: Double = 650.0
  private val dialogHeight: Double = 1000.0
  private val coverImageWidth: Double = 300.0
  private val coverImageHeight: Double = 300.0
}
