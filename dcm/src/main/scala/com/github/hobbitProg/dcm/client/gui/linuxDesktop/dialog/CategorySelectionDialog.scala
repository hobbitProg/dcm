package com.github.hobbitProg.dcm.client.gui.linuxDesktop.dialog

import javafx.collections.FXCollections
import javafx.scene.control.SelectionMode

import scala.List
import scala.collection.Set
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, ListView, MultipleSelectionModel}
import scalafx.scene.layout.AnchorPane
import scalafx.stage.Stage

import com.github.hobbitProg.dcm.client.control.CategorySelectionControl

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
  // The control to handle user inputs
  private val inputControl: CategorySelectionControl =
    new CategorySelectionControl()

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
  availableCategoriesControl.id =
    CategorySelectionDialog.availableCategoriesId
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
  selectedCategoriesControl.selectionModel.value.selectionMode =
    SelectionMode.MULTIPLE
  selectedCategoriesControl.id =
    CategorySelectionDialog.selectedCategoriesId
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
    CategorySelectionDialog.associateButtonId
  val availableCategoriesSelectionModel: MultipleSelectionModel[String] =
    availableCategoriesControl.selectionModel.value
  availableCategoriesSelectionModel.selectedItem.onChange {
    inputControl.determineButtonActivation(
      associateButton,
      availableCategoriesControl
    )
  }
  //noinspection ScalaUnusedSymbol
  associateButton.onAction =
    (event: ActionEvent) => {
      inputControl.moveCategories(
        availableCategoriesControl.selectionModel.value.getSelectedItems.toList,
        available,
        selected
      )
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
  disassociateButton.id =
    CategorySelectionDialog.disassociateButtonId
  disassociateButton.disable =
    true
  val selectedCategoriesSelectionModel: MultipleSelectionModel[String] =
    selectedCategoriesControl.selectionModel.value
  selectedCategoriesSelectionModel.selectedItem.onChange {
    inputControl.determineButtonActivation(
      disassociateButton,
      selectedCategoriesControl
    )
  }
  disassociateButton.onAction =
    (event: ActionEvent) => {
      inputControl.moveCategories(
        selectedCategoriesControl.selectionModel.value.getSelectedItems.toList,
        selected,
        available
      )
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
  //noinspection ScalaUnusedSymbol
  saveButton.onAction =
    (event: ActionEvent) => {
      finalizeAssociation()
      closeDialog()
    }

  AnchorPane.setTopAnchor(
    saveButton,
    CategorySelectionDialog.saveButtonTopBorder
  )
  AnchorPane.setLeftAnchor(
    saveButton,
    CategorySelectionDialog.saveButtonLeftBorder
  )

  // Create button to cancel associating categories
  private val cancelButton: Button =
    new Button(
      "Cancel"
    )
  //noinspection ScalaUnusedSymbol
  cancelButton.onAction =
    (event: ActionEvent) => {
      closeDialog()
    }
  AnchorPane.setTopAnchor(
    cancelButton,
    CategorySelectionDialog.cancelButtonTopBorder
  )
  AnchorPane.setLeftAnchor(
    cancelButton,
    CategorySelectionDialog.cancelButtonLeftBorder
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
          saveButton,
          cancelButton
        )
    }

  // Close dialog window
  private def closeDialog() = {
    val parentStage: Stage =
      window.value.asInstanceOf[javafx.stage.Stage]
    parentStage.close
  }

  // Finalize category associations for item
  private def finalizeAssociation() = {
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
  }
}

object CategorySelectionDialog {
  val associateButtonId: String = "AssociateButtonId"
  val disassociateButtonId: String = "DisassociateButtonId"
  val selectedCategoriesId: String = "SelectedCategoriesId"
  val availableCategoriesId: String = "AvailableCategoriesId"
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
  private val cancelButtonTopBorder: Double = 432.0
  private val cancelButtonLeftBorder: Double = 100.0

  private val dialogWidth: Double = 600.0
  private val dialogHeight: Double = 475.0
}
