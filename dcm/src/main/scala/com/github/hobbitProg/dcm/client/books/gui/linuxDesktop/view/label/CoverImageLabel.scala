package com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.view.label

import scalafx.scene.control.Label
import scalafx.scene.layout.AnchorPane

/**
  * Label for control that displays book cover
  * @author Kyle Cranmer
  * @since 0.1
  */
class CoverImageLabel
  extends Label(
    "Cover Image:"
  ) {
  AnchorPane.setLeftAnchor(
    this,
    CoverImageLabel.leftBorder
  )
  AnchorPane.setTopAnchor(
    this,
    CoverImageLabel.topBorder
  )
}

object CoverImageLabel {
  private val leftBorder: Double = 2.0
  private val topBorder: Double = 186.0
}
