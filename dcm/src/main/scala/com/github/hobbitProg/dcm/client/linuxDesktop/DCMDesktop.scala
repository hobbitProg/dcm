package com.github.hobbitProg.dcm.client.linuxDesktop

import scala.collection.Set

import scalafx.scene.control.TabPane

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.{Categories,
  BookCatalog}
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.dialog.ImageChooser

/**
  * Main desktop for distributed catalog manager
  * @author Kyle Cranmer
  * @since 0.1
  */
class DCMDesktop(
  private val coverChooser: ImageChooser,
  private val catalog: BookCatalog,
  private val service: BookCatalogService[BookCatalog],
  private val repository: BookCatalogRepository
)
  extends TabPane {

  prefWidth = DCMDesktop.width
  prefHeight = DCMDesktop.height

  // Add tab that contains book information
  val bookDisplay: BookTab =
    new BookTab(
      coverChooser,
      catalog,
      repository,
      service,
      repository.definedCategories
    )
  this += bookDisplay
}

object DCMDesktop {
  private val width = 740.0
  private val height = 445.0
}
