package com.github.hobbitProg.dcm.client.linuxDesktop

import scala.collection.Set

import scalafx.scene.control.TabPane

import com.github.hobbitProg.dcm.client.books.Categories
import com.github.hobbitProg.dcm.client.books.bookCatalog.Catalog

/**
  * Main desktop for distributed catalog manager
  * @author Kyle Cranmer
  * @since 0.1
  */
class DCMDesktop(
  private val bookCatalog: Catalog,
  private val bookCategories: Set[Categories]
)
  extends TabPane {
  // Add tab that contains book information
  this += new BookTab(
    bookCatalog,
    bookCategories
  )
}
