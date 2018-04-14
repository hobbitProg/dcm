package com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.view.label

import scalafx.scene.control.Label
import scalafx.scene.layout.AnchorPane

/**
  * Label for author control
  * @author Kyle Cranmer
  * @since 0.1
  */
class AuthorLabel
extends Label(
  "Author"
) {
  AnchorPane.setLeftAnchor(
    this,
    AuthorLabel.leftBorder
  )
  AnchorPane.setTopAnchor(
    this,
    AuthorLabel.topBorder
  )
}

object AuthorLabel {
  private val topBorder: Double = 30.0
  private val leftBorder: Double = 2.0

}
