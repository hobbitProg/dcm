package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogView
import scalafx.scene.Scene
import scalafx.scene.layout.GridPane

import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.view.BookCatalogView

/**
  * Scene to use to test control that displays book catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookCatalogScene(
  private val bookRepository: BookCatalogRepository
) extends Scene {
  // Create control that displays books within catalog
  val catalogControl: BookCatalogView =
    new BookCatalogView(
      bookRepository
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
