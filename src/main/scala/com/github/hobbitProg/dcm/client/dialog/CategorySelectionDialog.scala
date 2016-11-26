package com.github.hobbitProg.dcm.client.dialog

import javafx.collections.FXCollections
import javafx.scene.control.SelectionMode
import scala.collection.Set
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, ListView, MultipleSelectionModel}
import scalafx.scene.layout.AnchorPane
import scalafx.stage.Stage

/**
  * Selects categories associated with entry
  * @author Kyle Cranmer
  * @since 0.1
  */
class CategorySelectionDialog(
  private val availableCategories: ObservableBuffer[String],
  private val selectedCategories: ObservableBuffer[String]
) extends Scene(
  CategorySelectionDialog.dialogWidth,
  CategorySelectionDialog.dialogHeight
) {
  // Locally selected/available categories
  private val available: ObservableBuffer[String] =
    FXCollections.observableArrayList(
      availableCategories.toList: _*
    )
  private val selected: ObservableBuffer[String] =
    FXCollections.observableArrayList(
      selectedCategories.toList: _*
    )

  // Create control for available categories
  private val availableLabel: Label =
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
  private val availableCategoriesControl: ListView[String] =
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
  private val selectedLabel: Label =
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
  private val selectedCategoriesControl: ListView[String] =
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

  // Create button to associate categories with entry
  private val associateButton: Button =
    new Button(
      "->"
    )
  associateButton.disable =
    true
  associateButton.id =
    CategorySelectionDialog.availableButtonId
  val availableCategoriesSelectionModel: MultipleSelectionModel[String] =
    availableCategoriesControl.selectionModel.value
  availableCategoriesSelectionModel.selectedItem.onChange {
    associateButton.disable =
      availableCategoriesControl.selectionModel.value.isEmpty
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

  // Create button to disassociate categories with entry
  private val disassociateButton: Button =
    new Button(
      "<-"
    )
  disassociateButton.disable =
    true
  val selectedCategoriesSelectionModel: MultipleSelectionModel[String] =
    selectedCategoriesControl.selectionModel.value
  selectedCategoriesSelectionModel.selectedItem.onChange {
    disassociateButton.disable =
      selectedCategoriesControl.selectionModel.value.isEmpty
  }
  disassociateButton.onAction =
    (event: ActionEvent) => {
      selected --=
        selectedCategoriesControl.selectionModel.value.getSelectedItems.toList
      available ++=
        selectedCategoriesControl.selectionModel.value.getSelectedItems.toList
    }
  AnchorPane.setTopAnchor(
    disassociateButton,
    CategorySelectionDialog.disassociateButtonTopBorder
  )
  AnchorPane.setLeftAnchor(
    disassociateButton,
    CategorySelectionDialog.disassociatedButtonLeftBorder
  )

  // Create button to commit category associations
  private val saveButton: Button =
    new Button(
      "Save"
    )
  saveButton.id =
    CategorySelectionDialog.saveButtonId
  saveButton.onAction =
    (event: ActionEvent) => {
      val newlySelectedCategories: Set[String] =
        availableCategories.toSet[String] -- available.toSet[String]
      val newlyDeselectedCategories: Set[String] =
        available.toSet[String] -- availableCategories.toSet[String]
      availableCategories --=
        newlySelectedCategories
      availableCategories ++=
        newlyDeselectedCategories
      availableCategories sort {
        (categoryOne, categoryTwo) =>
          (categoryOne compare categoryTwo) < 0
      }
      selectedCategories --=
        newlyDeselectedCategories
      selectedCategories ++=
        newlySelectedCategories
      selectedCategories sort {
        (categoryOne, categoryTwo) =>
          (categoryOne compare categoryTwo) < 0
      }
      val parentStage: Stage =
        window.value.asInstanceOf[javafx.stage.Stage]
      parentStage.close
    }
  AnchorPane.setTopAnchor(
    saveButton,
    CategorySelectionDialog.saveButtonTopBorder
  )
  AnchorPane.setLeftAnchor(
    saveButton,
    CategorySelectionDialog.saveButtonLeftBorder
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
          associateButton,
          disassociateButton,
          saveButton
        )
    }
}

object CategorySelectionDialog {
  val availableButtonId: String = "AvailableButtonId"
  val saveButtonId: String = "SaveButtonId"

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
  private val disassociateButtonTopBorder: Double = 250.0
  private val disassociatedButtonLeftBorder: Double = 255.0
  private val saveButtonTopBorder: Double = 432.0
  private val saveButtonLeftBorder: Double = 390.0

  private val dialogWidth: Double = 600.0
  private val dialogHeight: Double = 475.0
}
