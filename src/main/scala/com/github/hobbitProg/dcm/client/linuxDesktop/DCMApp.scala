package com.github.hobbitProg.dcm.client.linuxDesktop

import scala.collection.Set
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.stage.FileChooser
import com.github.hobbitProg.dcm.client.books.Categories
import com.github.hobbitProg.dcm.client.books.bookCatalog.Catalog
import com.sun.org.apache.xml.internal.resolver.CatalogManager

/**
  * Main program to run distributed catalog manager on Linux desktop
  * @author Kyle Cranmer
  * @since 0.1
  */
class DCMApp(
  private val bookCatalog: Catalog,
  private val definedBookCategories: Set[Categories]
)
  extends JFXApp {
  stage = new PrimaryStage {
    scene = new Scene {
      root = new DCMDesktop(
        new FileChooser(),
        bookCatalog,
        definedBookCategories
      )
    }
  }
}
