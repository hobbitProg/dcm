package com.github.hobbitProg.dcm.client.control

import scalafx.scene.control.{Label, TextArea, TextField, TextInputControl}
import scalafx.scene.layout.AnchorPane
import scalafx.scene.Group

import com.github.hobbitProg.dcm.client.books.bookCatalog.Book

/**
  * Control that displays information on currently selected book
  * @author Kyle Cranmer
  * @since 0.1
  */
class SelectedBookControl
  extends Group {
  // Create control to display title of book
  private val titleLabel: Label =
    new Label(
      "Title"
    )
  AnchorPane.setTopAnchor(
    titleLabel,
    SelectedBookControl.titleTopBorder
  )
  AnchorPane.setLeftAnchor(
    titleLabel,
    SelectedBookControl.labelLeftBorder
  )

  private val titleValue: TextField =
    new TextField
  titleValue.editable = false
  AnchorPane.setTopAnchor(
    titleValue,
    SelectedBookControl.titleTopBorder
  )
  AnchorPane.setLeftAnchor(
    titleValue,
    SelectedBookControl.textFieldLeftBorder
  )

  // Create control to display author of book
  private val authorLabel: Label =
    new Label(
      "Author"
    )
  AnchorPane.setTopAnchor(
    authorLabel,
    SelectedBookControl.authorTopBorder
  )
  AnchorPane.setLeftAnchor(
    authorLabel,
    SelectedBookControl.labelLeftBorder
  )

  private val authorValue: TextField =
    new TextField
  authorValue.editable = false
  AnchorPane.setTopAnchor(
    authorValue,
    SelectedBookControl.authorTopBorder
  )
  AnchorPane.setLeftAnchor(
    authorValue,
    SelectedBookControl.textFieldLeftBorder
  )

  // Create control to display ISBN of book
  private val isbnLabel: Label =
    new Label(
      "ISBN"
    )
  AnchorPane.setTopAnchor(
    isbnLabel,
    SelectedBookControl.isbnTopBorder
  )
  AnchorPane.setLeftAnchor(
    isbnLabel,
    SelectedBookControl.labelLeftBorder
  )

  private val isbnValue: TextField =
    new TextField
  isbnValue.editable = false
  AnchorPane.setTopAnchor(
    isbnValue,
    SelectedBookControl.isbnTopBorder
  )
  AnchorPane.setLeftAnchor(
    isbnValue,
    SelectedBookControl.textFieldLeftBorder
  )

  // Create control for display book description
  val descriptionLabel: Label =
    new Label(
      "Description:"
    )
  AnchorPane.setTopAnchor(
    descriptionLabel,
    SelectedBookControl.descriptionLabelTopBorder
  )
  AnchorPane.setLeftAnchor(
    descriptionLabel,
    SelectedBookControl.labelLeftBorder
  )

  val descriptionValue: TextArea =
    new TextArea
  descriptionValue.editable = false
  AnchorPane.setTopAnchor(
    descriptionValue,
    SelectedBookControl.descrptionControlTopBorder
  )
  AnchorPane.setLeftAnchor(
    descriptionValue,
    SelectedBookControl.descriptionControlLeftBorder
  )

  // Set pane for dialog
  children =
    new AnchorPane {
      children =
        List(
          titleLabel,
          titleValue,
          authorLabel,
          authorValue,
          isbnLabel,
          isbnValue,
          descriptionLabel,
          descriptionValue
        )
    }

  /**
    * Display book selected by user
    * @param selectedBook Book selected by user
    */
  def display(
    selectedBook: Book
  ): Unit = {
    updateValue(
      titleValue,
      selectedBook.title
    )
    updateValue(
      authorValue,
      selectedBook.author
    )
    updateValue(
      isbnValue,
      selectedBook.isbn
    )
    updateValue(
      descriptionValue,
      selectedBook.description
    )
  }

  /**
    * Update value displayed in field
    * @param valueField Field to update
    * @param newValue Value to place intofield
    */
  private def updateValue(
    valueField: TextInputControl,
    newValue: String
  ) = {
    valueField.editable = true
    valueField.text = newValue
    valueField.editable = false

  }
}

object  SelectedBookControl {
  private val titleTopBorder: Double = 2.0
  private val authorTopBorder: Double = 30.0
  private val labelLeftBorder: Double = 2.0
  private val descriptionLabelTopBorder: Double = 86.0
  private val descriptionControlLeftBorder: Double = 2.0
  private val descrptionControlTopBorder: Double = 114.0
  private val textFieldLeftBorder: Double = 90.0
  private val isbnTopBorder: Double = 58.0
}
