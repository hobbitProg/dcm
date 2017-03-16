package com.github.hobbitProg.dcm.client.linuxDesktop

import scala.collection.Set

import scalafx.scene.control.TabPane
import scalafx.stage.FileChooser

import com.github.hobbitProg.dcm.client.books.Categories
import com.github.hobbitProg.dcm.client.books.bookCatalog.Catalog
//import com.github.hobbitProg.dcm.client.books.bookCatalog.storage.Storage

/**
  * Main desktop for distributed catalog manager
  * @author Kyle Cranmer
  * @since 0.1
  */
class DCMDesktop(
  private val coverChooser: FileChooser
//  private val catalogStorage: Storage
)
  extends TabPane {

  prefWidth = DCMDesktop.width
  prefHeight = DCMDesktop.height

  // Add tab that contains book information
//  this += new BookTab(
//    coverChooser,
//    new Catalog(
//      catalogStorage
//    ),
//    catalogStorage.definedCategories
//  )
}

object DCMDesktop {
  private val width = 740.0
  private val height = 445.0
}
