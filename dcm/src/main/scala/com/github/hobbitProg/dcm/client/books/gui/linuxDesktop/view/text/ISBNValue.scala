package com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.view.text

import scalafx.scene.control.TextField
import scalafx.scene.layout.AnchorPane

/**
  * Control containing ISBN for book
  * @author Kyle Cranmer
  * @since 0.1
  */
class ISBNValue
  extends TextField {
  AnchorPane.setLeftAnchor(
    this,
    ISBNValue.leftBorder
  )
  AnchorPane.setTopAnchor(
    this,
    ISBNValue.topBorder
  )
}

object ISBNValue {
  private val leftBorder: Double = 90.0
  private val topBorder: Double = 58.0

}
