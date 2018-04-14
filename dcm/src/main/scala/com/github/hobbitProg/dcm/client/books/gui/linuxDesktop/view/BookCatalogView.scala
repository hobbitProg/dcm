package com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.view

import javafx.scene.control.SelectionMode

import scala.math.Ordering.StringOrdering

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.collections.transformation.SortedBuffer
import scalafx.scene.control.{ListCell, ListView}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.{Book,
  BookCatalog}
import BookCatalog._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.control.BookCatalogControl

/**
  * View to display books in catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookCatalogView(
  private val repository: BookCatalogRepository
) extends ListView[Book]
    with StringOrdering {

  // Control for the view
  private val control: BookCatalogControl =
    new BookCatalogControl()

  // Only display title of book in control
  cellFactory = {
    _ =>
      new ListCell[Book] {
        item.onChange {
          (_, _, newBook) =>
          control.displayBookInCell(
            items.value,
            this,
            newBook
          )
        }
      }
  }

  // Have books be sorted by title
  items =
    new ObservableBuffer[Book]

  // Only allow one book to be selected
  selectionModel.value.setSelectionMode(
    SelectionMode.SINGLE
  )

  // Display books that are initially in catalog
  for (initialBook <- repository.contents) {
    items.value += initialBook
  }
  items.value sort {
    (left: Book, right: Book) =>
      compare(
        left.title, right.title
      )
  }

  /**
    * Register view to update when catalog is modified
    * @param catalog Book catalog being worked on
    * @return Catalog that notifies view when catalog changes
    */
  def register(
    catalog: BookCatalog
  ): BookCatalog = {
    // Display all books that are added to catalog
    val updatedCatalog =
      onAdd(
        catalog,
        newBook =>
        control.displayNewBook(
          items.value,
          newBook
        )
      )

    // Replace original book with modified book
    onModify(
      updatedCatalog,
      (originalBook, updatedBook) => {
        control.displayUpdatedBook(
          items.value,
          selectionModel.value,
          originalBook,
          updatedBook
        )
      }
    )
  }
}
