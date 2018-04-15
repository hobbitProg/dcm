package com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.view

import java.net.URI

import scalafx.scene.Group
import scalafx.scene.control._
import scalafx.scene.layout.AnchorPane

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.Book
import com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.view.image._
import com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.view.label._
import com.github.hobbitProg.dcm.client.books.view.listView._
import com.github.hobbitProg.dcm.client.books.view.text._

/**
  * View that displays information on currently selected book
  * @author Kyle Cranmer
  * @since 0.1
  */
class SelectedBookView
  extends Group {
  // Create control to display title of book
  private val titleLabel: TitleLabel =
    new TitleLabel
  private val titleValue: TitleValue =
    new TitleValue
  titleValue.editable = false
  titleValue.id = SelectedBookView.titleControlId

  // Create control to display author of book
  private val authorLabel: AuthorLabel =
    new AuthorLabel
  private val authorValue: AuthorValue =
    new AuthorValue
  authorValue.editable = false
  authorValue.id = SelectedBookView.authorControlId

  // Create control to display ISBN of book
  private val isbnLabel: ISBNLabel =
    new ISBNLabel
  private val isbnValue: ISBNValue =
    new ISBNValue
  isbnValue.editable = false
  isbnValue.id = SelectedBookView.isbnControlId

  // Create control for display book description
  private val descriptionLabel: DescriptionLabel =
    new DescriptionLabel
  private val descriptionValue: DescriptionValue =
    new DescriptionValue
  descriptionValue.editable = false
  descriptionValue.id = SelectedBookView.descriptionControlId

  // Create control to display cover image
  private val coverImageLabel: CoverImageLabel =
    new CoverImageLabel
  private val coverImageControl: CoverImage =
    new CoverImage
  coverImageControl.id = SelectedBookView.coverImageControlId

  // Create control to display categories associated with selected book
  private val categoryLabel: CategoryLabel =
    new CategoryLabel
  private val categoryControl: BookCategories =
    new BookCategories
  categoryControl.id = SelectedBookView.categoriesControlId

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
    selectedBook.description match {
      case Some(bookDescription) =>
        updateValue(
          descriptionValue,
          bookDescription
        )
      case None =>
    }
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
    * @param newValue Value to place into field
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

object SelectedBookView {
  val titleControlId = "titleControlId"
  val authorControlId = "authorControlId"
  val isbnControlId = "isbnControlId"
  val descriptionControlId = "descriptionControlId"
  val coverImageControlId = "coverImageControlId"
  val categoriesControlId = "categoriesControlId"
}
