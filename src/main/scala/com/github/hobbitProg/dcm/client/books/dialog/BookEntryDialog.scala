package com.github.hobbitProg.dcm.client.books.dialog

import java.io.{File, FileInputStream}

import javafx.scene.layout.{Border, BorderStroke, BorderStrokeStyle, CornerRadii}
import javafx.scene.paint.Color
import javafx.stage.FileChooser.ExtensionFilter

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, TextArea, TextField}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{AnchorPane, VBox}
import scalafx.stage.FileChooser
import com.github.hobbitProg.dcm.client.books.bookCatalog.Book

/**
  * Dialog for entering information on book for catalog
  * @author Kyle Cranmer
  * @since 0.1
  */

class BookEntryDialog(
  private val coverImageChooser: FileChooser
)
  extends Scene(
    BookEntryDialog.dialogWidth,
    BookEntryDialog.dialogHeight
  ) {
  // Book being edited
  private val bookBeingEdited =
    new Book

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
      coverImageControl.image =
        new Image(
          new FileInputStream(
            coverImageChooser.showOpenDialog(
              window.value
            )
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
    BookEntryDialog.coverImageSelectionTopBorser
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
          coverImagePane,
          coverImageSelectionButton
        )
    }
}

object BookEntryDialog {
  val titleControlId: String = "bookTitleControl"
  val authorControlId: String = "bookAuthorControl"
  val isbnControlId: String = "bookISBNControl"
  val descriptionControlId: String = "bookDescriptionControl"
  val bookCoverButtonId: String = "bookCoverButton"

  private val titleTopBorder: Double = 2.0
  private val authorTopBorder: Double = 30.0
  private val isbnTopBorder: Double = 58.0
  private val descriptionLabelTopBorder: Double = 86.0
  private val descriptionControlLeftBorder: Double = 2.0
  private val descrptionControlTopBorder: Double = 114.0
  private val labelLeftBorder: Double = 2.0
  private val textFieldLeftBorder: Double = 90.0
  private val coverImageLeftBorder: Double = 2.0
  private val coverImageTopBorder: Double = 296.0
  private val coverImageSelectionLeftBorder: Double = 145.0
  private val coverImageSelectionTopBorser: Double = 598.0

  private val dialogWidth: Double = 650.0
  private val dialogHeight: Double = 750.0
  private val coverImageWidth: Double = 300.0
  private val coverImageHeight: Double = 300.0
}
