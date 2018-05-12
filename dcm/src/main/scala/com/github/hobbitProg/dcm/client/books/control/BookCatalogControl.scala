package com.github.hobbitProg.dcm.client.books.control

import scala.math.Ordering.StringOrdering

import java.util.Comparator

import javafx.collections.FXCollections

import scalafx.application.Platform
import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ListCell, MultipleSelectionModel}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.Book

/**
  * Controller for view that displays books in catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class BookCatalogControl
    extends StringOrdering {
  // Orders books by title
  private object TitleOrdering
      extends Comparator[Book] {
    override def compare(
      book1: Book,
      book2: Book
    ) = {
      book1.title compareTo book2.title
    }
  }

  /**
    * Display a new book in a new cell within the book catalog view
    * @param displayedBooks The books that are currently being displayed in the
    * view
    * @param newCell The cell to display the new book
    * @param newBook The book that was just created
    */
  def displayBookInCell(
    displayedBooks: ObservableBuffer[Book],
    newCell: ListCell[Book],
    newBook: Book
  ) = Platform.runLater {
    if (newBook != null) {
      newCell.text =
        newBook.title
      FXCollections.sort(
        displayedBooks,
        TitleOrdering
      )
//      if (displayedBooks.length > 1) {
//        displayedBooks sort {
//          (left: Book, right: Book) =>
//          lt(
//            left.title, right.title
//          )
//        }
//      }
    }
    else {
      newCell.text = null
      newCell.graphic = null
    }
  }

  /**
    * Display book that was added to catalog
    * @param displayedBooks The books that are currently being displayed in the
    * view
    * @param newBook The book that was just created
    */
  def displayNewBook(
    displayedBooks: ObservableBuffer[Book],
    newBook: Book
  ) = {
    displayedBooks += newBook
    FXCollections.sort(
      displayedBooks,
      TitleOrdering
    )
  }

  /**
    * Update book that user just changed
    * @param displayedBooks The books that are currently being displayed in the
    * view
    * @param selectionModel Model for selecting books within control
    * @param originalBook Original book before modifications were made
    * @param updatedBook Updated book after modifications were made
    */
  def displayUpdatedBook(
    displayedBooks: ObservableBuffer[Book],
    selectionModel: MultipleSelectionModel[Book],
    originalBook: Book,
    updatedBook: Book
  ) = {
    displayedBooks += updatedBook
    displayedBooks -= originalBook
    FXCollections.sort(
      displayedBooks,
      TitleOrdering
    )

    // Clear selected book
    selectionModel.clearSelection()
  }

  def removeDeletedBook(
    displayedBooks: ObservableBuffer[Book],
    selectionModel: MultipleSelectionModel[Book],
    deletedBook: Book) = {
    displayedBooks -= deletedBook
    FXCollections.sort(
      displayedBooks,
      TitleOrdering
    )
    selectionModel.clearSelection()
  }

}
