package com.github.hobbitProg.dcm.client.control

import java.io.{File, FileInputStream}
import java.net.URI
import javafx.collections.FXCollections
import javafx.scene.layout.{Border, BorderStroke, BorderStrokeStyle, CornerRadii}
import javafx.scene.paint.Color

import scalafx.collections.ObservableBuffer
import scalafx.Includes._
import scalafx.scene.control.{Label, ListView, TextArea, TextField,
TextInputControl}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{AnchorPane, VBox}
import scalafx.scene.Group

import com.github.hobbitProg.dcm.client.books.bookCatalog.Book
import com.github.hobbitProg.dcm.client.control.model.DisableSelectionModel

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

  // Create control to display cover image
  val coverImageLabel: Label =
    new Label(
      "Cover Image:"
    )
  AnchorPane.setLeftAnchor(
    coverImageLabel,
    SelectedBookControl.coverImageLabelLeftBorder
  )
  AnchorPane.setTopAnchor(
    coverImageLabel,
    SelectedBookControl.coverImageLabelTopBorder
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
      minHeight = SelectedBookControl.coverImageHeight
      minWidth = SelectedBookControl.coverImageWidth
    }
  AnchorPane.setLeftAnchor(
    coverImagePane,
    SelectedBookControl.coverImageLeftBorder
  )
  AnchorPane.setTopAnchor(
    coverImagePane,
    SelectedBookControl.coverImageTopBorder
  )

  // Create control to display categories associated with selected book
  private val categoryLabel: Label =
    new Label(
      "Associated Categories:"
    )
  AnchorPane.setTopAnchor(
    categoryLabel,
    SelectedBookControl.categoryLabelTopBorder
  )
  AnchorPane.setLeftAnchor(
    categoryLabel,
    SelectedBookControl.categoryControlLeftBorder
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
    SelectedBookControl.categoryControlTopBorder
  )
  AnchorPane.setLeftAnchor(
    categoryControl,
    SelectedBookControl.categoryControlLeftBorder
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
          descriptionValue,
          coverImageLabel,
          coverImagePane,
          categoryLabel,
          categoryControl
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
    selectedBook.coverImage match {
      case Some(imageLocation: URI) =>
        coverImageControl.image =
          new Image(
            new FileInputStream(
              new File(
                imageLocation
              )
            ),
            SelectedBookControl.coverImageWidth,
            SelectedBookControl.coverImageHeight,
            true,
            true
          )
      case None =>
    }
    associatedCategories.addAll(
      selectedBook.categories.toList.sorted :_*
    )
  }

  /**
    * Clear information on selected information
    */
  def clear(): Unit = {
    updateValue(
      titleValue,
      ""
    )
    updateValue(
      authorValue,
      ""
    )
    updateValue(
      isbnValue,
      ""
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
  private val textFieldLeftBorder: Double = 90.0
  private val isbnTopBorder: Double = 58.0
  private val descriptionLabelTopBorder: Double = 86.0
  private val descriptionControlLeftBorder: Double = 2.0
  private val descrptionControlTopBorder: Double = 114.0
  private val coverImageLabelLeftBorder: Double = 2.0
  private val coverImageLabelTopBorder: Double = 296.0
  private val coverImageLeftBorder: Double = 2.0
  private val coverImageTopBorder: Double = 324.0
  private val categoryLabelLeftBorder: Double = 310.0
  private val categoryLabelTopBorder: Double = 296.0
  private val categoryControlLeftBorder: Double = 310.0
  private val categoryControlTopBorder: Double = 324.0

  private val coverImageWidth: Double = 300.0
  private val coverImageHeight: Double = 300.0
}
