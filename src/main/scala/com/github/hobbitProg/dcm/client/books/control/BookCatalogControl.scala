package com.github.hobbitProg.dcm.client.books.control

import scala.math.Ordering.StringOrdering

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ListCell, ListView}

import com.github.hobbitProg.dcm.client.books.bookCatalog.{Book, Catalog}

/**
  * Control to display books in catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookCatalogControl(
  private val source: Catalog
) extends ListView[Book]
  with StringOrdering {

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
              items.value sortWith {
                (left, right) =>
                  lt(
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

  // Display books that are initially in catalog
  for (initialBook <- source) {
    items.value += initialBook
  }
  items.value sortWith {
    (left, right) =>
      lt(
        left.title, right.title
      )
  }

  //noinspection ScalaUnusedSymbol
  // Display all books that are added to catalog
  private val additionSubscription =
    source onAdd {
      newBook =>
        items.value += newBook
    }
}
