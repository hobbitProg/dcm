package com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.view.label

import scalafx.scene.control.Label
import scalafx.scene.layout.AnchorPane

/**
  * Label for containing description of book
  * @author Kyle Cranmer
  * @since 0.1
  */
class DescriptionLabel
  extends Label(
    "Description:"
  ) {
  AnchorPane.setLeftAnchor(
    this,
    DescriptionLabel.leftBorder
  )
  AnchorPane.setTopAnchor(
    this,
    DescriptionLabel.topBorder
  )
}

object DescriptionLabel {
  private val topBorder: Double = 86.0
  private val leftBorder: Double = 2.0

}
