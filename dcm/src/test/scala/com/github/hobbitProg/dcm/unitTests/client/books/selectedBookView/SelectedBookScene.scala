package com.github.hobbitProg.dcm.unitTests.client.books.selectedBookView

import com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.view.
  SelectedBookView

import scalafx.scene.Scene
import scalafx.scene.layout.GridPane

/**
  * Scene to use to test control containing currently selected book
  */
class SelectedBookScene
  extends Scene(
    SelectedBookScene.sceneWidth,
    SelectedBookScene.sceneHeight
  ) {
  // Create control that displays books within catalog
  val bookControl: SelectedBookView =
    new SelectedBookView
  GridPane.setColumnIndex(
    bookControl,
    0
  )
  GridPane.setRowIndex(
    bookControl,
    0
  )

  content =
    new GridPane {
      children =
        List(
          bookControl
        )
    }
}

object SelectedBookScene {
  private val sceneWidth: Double = 650.0
  private val sceneHeight: Double = 1000.0
}
