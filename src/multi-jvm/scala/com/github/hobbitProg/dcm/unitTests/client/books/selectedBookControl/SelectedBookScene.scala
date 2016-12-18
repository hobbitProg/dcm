package com.github.hobbitProg.dcm.unitTests.client.books.selectedBookControl

import com.github.hobbitProg.dcm.client.books.control.SelectedBookControl

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
  val bookControl: SelectedBookControl =
    new SelectedBookControl
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
