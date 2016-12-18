package com.github.hobbitProg.dcm.client.books.control

import java.net.URI

import com.github.hobbitProg.dcm.client.books.bookCatalog.Book

import scalafx.scene.Group
import scalafx.scene.control._
import scalafx.scene.layout.AnchorPane

import com.github.hobbitProg.dcm.client.books.Conversions._
import com.github.hobbitProg.dcm.client.books.control.image._
import com.github.hobbitProg.dcm.client.books.control.label._
import com.github.hobbitProg.dcm.client.books.control.listView._
import com.github.hobbitProg.dcm.client.books.control.text._

/**
  * Control that displays information on currently selected book
  * @author Kyle Cranmer
  * @since 0.1
  */
class SelectedBookControl
  extends Group {
  // Create control to display title of book
  private val titleLabel: TitleLabel =
    new TitleLabel
  private val titleValue: TitleValue =
    new TitleValue
  titleValue.editable = false

  // Create control to display author of book
  private val authorLabel: AuthorLabel =
    new AuthorLabel
  private val authorValue: AuthorValue =
    new AuthorValue
  authorValue.editable = false

  // Create control to display ISBN of book
  private val isbnLabel: ISBNLabel =
    new ISBNLabel
  private val isbnValue: ISBNValue =
    new ISBNValue
  isbnValue.editable = false

  // Create control for display book description
  private val descriptionLabel: DescriptionLabel =
    new DescriptionLabel
  private val descriptionValue: DescriptionValue =
    new DescriptionValue
  descriptionValue.editable = false

  // Create control to display cover image
  private val coverImageLabel: CoverImageLabel =
    new CoverImageLabel
  private val coverImageControl: CoverImage =
    new CoverImage

  // Create control to display categories associated with selected book
  private val categoryLabel: CategoryLabel =
    new CategoryLabel
  private val categoryControl: BookCategories =
    new BookCategories

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
          coverImageControl,
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
          imageLocation
      case None =>
    }
    categoryControl.categories.addAll(
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
    updateValue(
      descriptionValue,
      ""
    )
    coverImageControl.image = null
    categoryControl.categories.clear()
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
