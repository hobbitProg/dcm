package com.github.hobbitProg.dcm.client.books.control.text

import scalafx.scene.control.TextArea
import scalafx.scene.layout.AnchorPane

/**
  * Control containing description of book
  * @author Kyle Cranmer
  * @since 0.1
  */
class DescriptionValue
  extends TextArea {
  AnchorPane.setLeftAnchor(
    this,
    DescriptionValue.leftBorder
  )
  AnchorPane.setTopAnchor(
    this,
    DescriptionValue.topBorder
  )
}

object DescriptionValue {
  private val leftBorder: Double = 2.0
  private val topBorder: Double = 114.0

}