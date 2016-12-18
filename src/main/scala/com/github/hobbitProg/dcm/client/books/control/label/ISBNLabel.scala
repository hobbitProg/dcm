package com.github.hobbitProg.dcm.client.books.control.label

import scalafx.scene.control.Label
import scalafx.scene.layout.AnchorPane

/**
  * Label for control containing ISBN for book
  * @author Kyle Cranmer
  * @since 0.1
  */
class ISBNLabel
  extends Label(
    "ISBN"
  ) {
  AnchorPane.setLeftAnchor(
    this,
    ISBNLabel.leftBorder
  )
  AnchorPane.setTopAnchor(
    this,
    ISBNLabel.topBorder
  )
}

object ISBNLabel {
  private val leftBorder: Double = 2.0
  private val topBorder: Double = 58.0
}
