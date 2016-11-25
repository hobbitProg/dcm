package com.github.hobbitProg.dcm.client.control.java;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

/**
 * Model that does not allow user to select any elements from control
 */
public class DisabledSelectionModel<ListContentType> extends
  MultipleSelectionModel<ListContentType> {
  /**
   * Returns read-only ObservableList of all selected indices
   * @return Empty list of indices
   */
  @Override
  public ObservableList<Integer> getSelectedIndices() {
    return
      FXCollections.emptyObservableList();
  }

  /**
   * Returns read-only ObservableList of all selected items
   * @return Empty list of elected items
   */
  @Override
  public ObservableList<ListContentType> getSelectedItems() {
    return
      FXCollections.emptyObservableList();
  }

  /**
   * Select given items at same time
   * @param index First index of item to select
   * @param indices Other indices of items to select
   */
  @Override
  public void selectIndices(
    int index,
    int... indices
  ) {
  }

  /**
   * Select all items
   */
  @Override
  public void selectAll() {
  }

  /**
   * Clear any selection prior to setting selection to given index
   * @param index Index of item to select
   */
  @Override
  public void clearAndSelect(
    int index
  ) {
  }

  /**
   * Select given index in selection model
   * @param index Index of item to select
   */
  @Override
  public void select(
    int index
  ) {
  }

  /**
   * Select index that contains given object
   * @param obj Item to select
   */
  @Override
  public void select(
    Object obj
  ) {
  }

  /**
   * Clear selection of item in given index
   * @param index Index of item to clear
   */
  @Override
  public void clearSelection(
    int index
  ) {
  }

  /**
   * Clears all selected items
   */
  @Override
  public void clearSelection() {
  }

  /**
   * Indicate if given index is currently selected
   * @param index Index of item being examined
   * @return Always false
   */
  @Override
  public boolean isSelected(int index) {
    return false;
  }

  /**
   * Indicates if no items are selected
   * @return Always true
   */
  @Override
  public boolean isEmpty() {
    return true;
  }

  /**
   * Select index directly before currently focused index.
   */
  @Override
  public void selectPrevious() {
  }

  /**
   * Select index directly after currently focused index
   */
  @Override
  public void selectNext() {
  }

  /**
   * Select first index in control
   */
  @Override
  public void selectFirst() {

  }

  /**
   * Select last index in control
   */
  @Override
  public void selectLast() {
  }
}
