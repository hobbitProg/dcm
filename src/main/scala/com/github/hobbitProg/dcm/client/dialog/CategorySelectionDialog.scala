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
  val availableCategories: ObservableBuffer[String],
  val selectedCategories: ObservableBuffer[String]
) extends Scene {
  // Locally selected/available categories
  val available: ObservableBuffer[String] =
    FXCollections.observableArrayList(
      availableCategories.toList: _*
    )
  val selected: ObservableBuffer[String] =
    FXCollections.observableArrayList(
      selectedCategories.toList: _*
    )

  // Create control for available categories
  val availableLabel: Label =
    new Label(
      "Available categories:"
    )
  AnchorPane.setLeftAnchor(
    availableLabel,
    CategorySelectionDialog.availableCategoriesLabelLeftBorder
  )
  AnchorPane.setTopAnchor(
    availableLabel,
    CategorySelectionDialog.availableCategoriesLabelTopBorder
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

  // Create control for associated categories
  val selectedLabel: Label =
    new Label(
      "Selected categories:"
    )
  AnchorPane.setLeftAnchor(
    selectedLabel,
    CategorySelectionDialog.selectedCategoriesLabelLeftBorder
  )
  AnchorPane.setTopAnchor(
    selectedLabel,
    CategorySelectionDialog.selectedCategoriesLabelTopBorder
  )
  val selectedCategoriesControl: ListView[String] =
    new ListView[String](
      selected
    )
  AnchorPane.setLeftAnchor(
    selectedCategoriesControl,
    CategorySelectionDialog.selectedCategoriesLeftBorder
  )
  AnchorPane.setTopAnchor(
    selectedCategoriesControl,
    CategorySelectionDialog.selectedCategoriesTopBorder
  )

  // Set pane for dialog
  content =
    new AnchorPane {
      children =
        List(
          availableLabel,
          availableCategoriesControl,
          selectedLabel,
          selectedCategoriesControl
        )
    }
}

object CategorySelectionDialog {
  private val availableCategoriesLabelTopBorder: Double = 2.0
  private val availableCategoriesLabelLeftBorder: Double = 2.0
  private val availableCategoriesTopBorder: Double = 30.0
  private val availableCategoriesLeftBorder: Double = 2.0
  private val selectedCategoriesLabelTopBorder: Double = 2.0
  private val selectedCategoriesLabelLeftBorder: Double = 250.0
  private val selectedCategoriesTopBorder: Double = 30.0
  private val selectedCategoriesLeftBorder: Double = 250.0
}
