package com.github.hobbitProg.dcm.client.books.view.listView

import javafx.collections.FXCollections

import scalafx.collections.ObservableBuffer
import scalafx.Includes._
import scalafx.scene.control.ListView
import scalafx.scene.layout.AnchorPane

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.Categories
import com.github.hobbitProg.dcm.client.control.javaRepresentation.DisabledSelectionModel

/**
  * Control that displays categories associated with book
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookCategories
  extends ListView[Categories] {
  /**
    * Categories associated with book
    */
  val categories: ObservableBuffer[Categories] =
    new ObservableBuffer[Categories](
      FXCollections.observableArrayList()
    )
  items =
    categories
  selectionModel =
    new DisabledSelectionModel[Categories]
  AnchorPane.setTopAnchor(
    this,
    BookCategories.topBorder
  )
  AnchorPane.setLeftAnchor(
    this,
    BookCategories.leftBorder
  )

  prefHeight = BookCategories.height
}

object BookCategories {
  private val leftBorder: Double = 170.0
  private val topBorder: Double = 204.0

  private val height: Double = 160.0
}


