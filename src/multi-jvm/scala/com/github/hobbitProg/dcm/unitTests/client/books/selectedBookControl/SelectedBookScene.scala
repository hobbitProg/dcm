package com.github.hobbitProg.dcm.unitTests.client.books.selectedBookControl

import scalafx.scene.Scene
import scalafx.scene.layout.GridPane

import com.github.hobbitProg.dcm.client.control.SelectedBookControl

/**
  * Scene to use to test control containing currently selected book
  */
class SelectedBookScene
  extends Scene {
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
