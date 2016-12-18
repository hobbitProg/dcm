package com.github.hobbitProg.dcm.client.books.control.text

import scalafx.scene.control.TextField
import scalafx.scene.layout.AnchorPane

/**
  * Text field containing title of book
  * @author Kyle Cranmer
  * @since 0.1
  */
class TitleValue
  extends TextField {
  AnchorPane.setLeftAnchor(
    this,
    TitleValue.leftBorder
  )
  AnchorPane.setTopAnchor(
    this,
    TitleValue.topBorder
  )
}

object TitleValue {
  private val topBorder: Double = 2.0
  private val leftBorder: Double = 90.0
}
