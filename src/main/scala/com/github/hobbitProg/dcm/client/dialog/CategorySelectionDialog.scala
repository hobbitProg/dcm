package com.github.hobbitProg.dcm.client.dialog

import javafx.collections.FXCollections
import javafx.scene.control.SelectionMode
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, ListView}
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
  availableCategoriesControl.selectionModel.value.selectionMode =
    SelectionMode.MULTIPLE
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

  // Create button to associate category with entry
  val associateButton: Button =
    new Button(
      "->"
    )
  associateButton.disable =
    false
  associateButton.id =
    CategorySelectionDialog.availableButtonId
  selectedCategoriesControl.selectionModel.onChange {
    associateButton.disable =
      selectedCategoriesControl.selectionModel.value.isEmpty
  }
  associateButton.onAction =
    (event: ActionEvent) => {
      selected ++=
        availableCategoriesControl.selectionModel.value.getSelectedItems.toList
      available --=
        availableCategoriesControl.selectionModel.value.getSelectedItems.toList
    }
  AnchorPane.setTopAnchor(
    associateButton,
    CategorySelectionDialog.associateButtonTopBorder
  )
  AnchorPane.setLeftAnchor(
    associateButton,
    CategorySelectionDialog.associateButtonLeftBorder
  )


  // Set pane for dialog
  content =
    new AnchorPane {
      children =
        List(
          availableLabel,
          availableCategoriesControl,
          selectedLabel,
          selectedCategoriesControl,
          associateButton
        )
    }
}

object CategorySelectionDialog {
  val availableButtonId: String = "AvailableButtonId"

  private val availableCategoriesLabelTopBorder: Double = 2.0
  private val availableCategoriesLabelLeftBorder: Double = 2.0
  private val availableCategoriesTopBorder: Double = 30.0
  private val availableCategoriesLeftBorder: Double = 2.0
  private val selectedCategoriesLabelTopBorder: Double = 2.0
  private val selectedCategoriesLabelLeftBorder: Double = 250.0
  private val selectedCategoriesTopBorder: Double = 30.0
  private val selectedCategoriesLeftBorder: Double = 295.0
  private val associateButtonTopBorder: Double = 200.0
  private val associateButtonLeftBorder: Double = 255.0
}
