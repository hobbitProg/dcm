package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogControl

import scalafx.scene.Scene
import scalafx.scene.layout.GridPane

import com.github.hobbitProg.dcm.client.books.bookCatalog.Catalog
import com.github.hobbitProg.dcm.client.control.BookCatalogControl

/**
  * Scene to use to test control that displays book catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookCatalogScene(
  private val bookCatalog: Catalog
) extends Scene {
  // Create control that displays books within catalog
  val catalogControl: BookCatalogControl =
    new BookCatalogControl(
      bookCatalog
    )
  catalogControl.id = BookCatalogScene.catalogControlId
  GridPane.setColumnIndex(
    catalogControl,
    0
  )
  GridPane.setRowIndex(
    catalogControl,
    0
  )

  content =
    new GridPane {
      children =
        List(
          catalogControl
        )
    }
}

object BookCatalogScene {
  val catalogControlId = "CatalogControl"
}