package com.github.hobbitProg.dcm.scalafx

import scalafx.Includes._
import javafx.scene.Node
import scalafx.scene.layout.AnchorPane
import scalafx.scene.Scene

/**
  * Retrieves the requested control from the given window
  * @author Kyle Cranmer
  * @since 0.2
  */
trait ControlRetriever {
  /**
    * Retrieve the save button from the given dialog
    *
    * @param testDialog Dialog being tested
    *
    * @return Either the save button from the given dialog or None if no save
    * button exists
    */
  def retrieveSaveButton(
    testDialog: Scene
  ): Option[Node] = {
    val dialogPane: AnchorPane =
      testDialog.content.head.asInstanceOf[javafx.scene.layout.AnchorPane]
    dialogPane.children.find {
      childControl =>
      childControl match {
        case childButton: javafx.scene.control.Button =>
          childButton.getText == "Save"
        case _ => false
      }
    }
  }
}
