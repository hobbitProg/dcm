package com.github.hobbitProg.dcm.matchers.scalafx

import org.hamcrest.Matcher

import javafx.scene.control.Cell
import javafx.scene.control.{ListCell, ListView}

import scala.collection.Set

import scalafx.Includes._

import org.testfx.api.FxAssert
import org.testfx.matcher.base.GeneralMatchers.typeSafeMatcher
import org.testfx.service.finder.NodeFinder
import org.testfx.service.query.NodeQuery

import com.github.hobbitProg.dcm.JavaConversions._

/**
  * Matcher to find list view cells
  * @author Kyle Cranmer
  * @since 0.2
  */
object ListViewCellMatcher {
  /**
    * Create a matcher to find a list cell on a given list view that is
    * displaying given text
    * @param parentId The list view containing the desired cell
    * @param requestedText The text the list cell should be displayed
    * @returns The matcher to find the list cell
    */
  def hasText[CellContentType](
    parentId: String,
    requestedText: String
  ) : Matcher[ListCell[CellContentType]] = {
    val descriptionText: String =
      "contains \"" + requestedText + "\""
    typeSafeMatcher(
      classOf[ListCell[CellContentType]],
      descriptionText,
      {
        cell: ListCell[CellContentType] =>
        hasText(
          cell,
          requestedText
        ) &&
        onListView(
          cell,
          parentId
        )
      }
    )
  }

  // Determine if a given cell has the given text
  private def hasText[CellContentType](
    cell: ListCell[CellContentType],
    requestedText: String
  ): Boolean =
    cell.text.value == requestedText

  // Determine if a given cell is on a given list view
  private def onListView[CellContentType](
    cell: ListCell[CellContentType],
    viewId: String
  ): Boolean =
    cell.listView.value.id.value == viewId
}
