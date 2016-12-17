package com.github.hobbitProg.dcm.client.control.model

import scalafx.scene.control.MultipleSelectionModel

/**
  * Model that does not allow user to select any elements from control
  * @author Kyle Cranmer
  * @since 0.1
  */
class DisableSelectionModel[ListControlType]
extends MultipleSelectionModel[ListControlType](
  new com.github.hobbitProg.dcm.client.control.javaRepresentation.DisabledSelectionModel[ListControlType]()
) {
}
