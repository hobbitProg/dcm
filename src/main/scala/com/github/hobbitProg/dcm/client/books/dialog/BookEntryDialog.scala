package com.github.hobbitProg.dcm.client.books.dialog

import scalafx.scene.Scene
import scalafx.scene.control.{Label, TextField}
import scalafx.scene.layout.AnchorPane
import com.github.hobbitProg.dcm.client.books.bookCatalog.Book

/**
  * Dialog for entering information on book for catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookEntryDialog
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
          isbnControl
        )
    }
}

object BookEntryDialog {
  val titleControlId: String = "bookTitleControl"
  val authorControlId: String = "bookAuthorControl"
  val isbnControlId: String = "bookISBNControl"

  private val titleTopBorder: Double = 2.0
  private val authorTopBorder: Double = 30.0
  private val isbnTopBorder: Double = 58.0
  private val labelLeftBorder: Double = 2.0
  private val textFieldLeftBorder: Double = 45.0

  private val dialogWidth: Double = 225.0
  private val dialogHeight: Double = 100.0
}
