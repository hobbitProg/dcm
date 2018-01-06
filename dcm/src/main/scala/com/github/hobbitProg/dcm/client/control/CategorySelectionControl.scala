package com.github.hobbitProg.dcm.client.control

import scala.List

import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Button, ListView}

/**
  * Control to handle user inputs for the category selection dialog
  * @author Kyle Cranmer
  * @since 0.2
  */
class CategorySelectionControl {
  /**
    * Determine if a given button is active depending on if the associated
    * categories view is populated
    * @param associatedButton The button to activate/deactivate
    * @param categoriesView The category view associated with the button
    */
  def determineButtonActivation(
    associatedButton: Button,
    categoriesView: ListView[String]
  ) = {
    associatedButton.disable =
      categoriesView.selectionModel.value.isEmpty
  }

  /**
    * Move the categories from one category view to another
    * @param categoriesToMove The categories to move between the category
    * views
    * @param origin The category view the categories are currently in
    * @param origin The category view the categories should be in
    */
  def moveCategories(
    categoriesToMove: List[String],
    origin: ObservableBuffer[String],
    destination: ObservableBuffer[String]
  ) = {
    origin --= categoriesToMove
    destination ++= categoriesToMove
  }
}
