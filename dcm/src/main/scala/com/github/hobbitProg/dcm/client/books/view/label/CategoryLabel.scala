package com.github.hobbitProg.dcm.client.books.view.label

import scalafx.scene.control.Label
import scalafx.scene.layout.AnchorPane

/**
  * Label for control that displays book's categories
  * @author Kyle Cranmer
  * @since 0.1
  */
class CategoryLabel
  extends Label(
    "Associated Categories:"
  ) {
  AnchorPane.setTopAnchor(
    this,
    CategoryLabel.topBorder
  )
  AnchorPane.setLeftAnchor(
    this,
    CategoryLabel.leftBorder
  )
}

object CategoryLabel {
  private val leftBorder: Double = 170.0
  private val topBorder: Double = 186.0
}
