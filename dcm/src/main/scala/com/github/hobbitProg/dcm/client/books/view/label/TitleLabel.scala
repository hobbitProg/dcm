package com.github.hobbitProg.dcm.client.books.view.label

import scalafx.scene.control.Label
import scalafx.scene.layout.AnchorPane

/**
  * Label for control containing title of book
  * @author Kyle Cranmer
  * @since 0.1
  */
class TitleLabel
  extends Label(
    "Title"
  ) {
  AnchorPane.setLeftAnchor(
    this,
    TitleLabel.leftBorder
  )
  AnchorPane.setTopAnchor(
    this,
    TitleLabel.topBorder
  )
}

object TitleLabel {
  private val topBorder: Double = 2.0
  private val leftBorder: Double = 2.0

}
