package com.github.hobbitProg.dcm.client.dialog

import javafx.collections.FXCollections
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.{Label, ListView}
import scalafx.scene.layout.AnchorPane

/**
  * Selects categories associated with entry
  * @author Kyle Cranmer
  * @since 0.1
  */
class CategorySelectionDialog(
  val availableCategories: ObservableBuffer[String]
) extends Scene {
  // Locally selected/available categories
  val available: ObservableBuffer[String] =
    FXCollections.observableArrayList(
      availableCategories.toList: _*
    )

  // Create control for available categories
  val availableLabel: Label =
    new Label(
      "Available categories:"
    )
  AnchorPane.setLeftAnchor(
    availableLabel,
    CategorySelectionDialog.topLabelLeftBorder
  )
  AnchorPane.setTopAnchor(
    availableLabel,
    CategorySelectionDialog.topLabelTopBorder
  )
  val availableCategoriesControl: ListView[String] =
    new ListView[String](
      available
    )
  AnchorPane.setLeftAnchor(
    availableCategoriesControl,
    CategorySelectionDialog.availableCategoriesLeftBorder
  )
  AnchorPane.setTopAnchor(
    availableCategoriesControl,
    CategorySelectionDialog.availableCategoriesTopBorder
  )

  // Set pane for dialog
  content =
    new AnchorPane {
      children =
        List(
          availableLabel,
          availableCategoriesControl
        )
    }
}

object CategorySelectionDialog {
  private val topLabelTopBorder: Double = 2.0
  private val topLabelLeftBorder: Double = 2.0
  private val availableCategoriesTopBorder: Double = 30
  private val availableCategoriesLeftBorder: Double = 2
}
