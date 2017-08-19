package com.github.hobbitProg.dcm.client.books.control

import javafx.scene.control.SelectionMode

import scala.math.Ordering.StringOrdering

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.collections.transformation.SortedBuffer
import scalafx.scene.control.{ListCell, ListView}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.Book
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.BookCatalog


/**
  * Control to display books in catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookCatalogControl(
  private val source: BookCatalog,
  private val repository: BookRepository
) extends ListView[Book]
    with StringOrdering {

  import source._

  // Only display title of book in control
  cellFactory = {
    _ =>
      new ListCell[Book] {
        item.onChange {
          (_, _, newBook) =>
            Platform runLater {
              if (newBook != null) {
                text =
                  newBook.title
              }
              items.value sort {
                (left: Book, right: Book) =>
                  compare(
                    left.title, right.title
                  )
              }
            }
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

  // Display all books that are added to catalog
  onAdd {
    newBook =>
      items.value += newBook
      items.value sort {
        (left: Book, right: Book) =>
          compare(
            left.title, right.title
          )
      }
  }
}
