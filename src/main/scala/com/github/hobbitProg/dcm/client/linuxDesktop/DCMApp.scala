package com.github.hobbitProg.dcm.client.linuxDesktop

import scala.collection.Set

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.stage.FileChooser

import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.interpreter.DatabaseBookRepositoryInterpreter
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.BookCatalog

/**
  * Main program to run distributed catalog manager on Linux desktop
  * @author Kyle Cranmer
  * @since 0.1
  */
class DCMApp
  extends JFXApp {
  stage = new PrimaryStage {
    scene = new Scene {
      root = new DCMDesktop(
        new FileChooser(),
        BookCatalog,
        DatabaseBookRepositoryInterpreter
      )
    }
  }
}
