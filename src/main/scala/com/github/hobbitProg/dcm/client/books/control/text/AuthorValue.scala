package com.github.hobbitProg.dcm.client.books.control.text

import scalafx.scene.control.TextField
import scalafx.scene.layout.AnchorPane

/**
  * Text control that contains author that wrote book
  * @author Kyle Cranmer
  * @since 0.1
  */
class AuthorValue
  extends TextField{
  AnchorPane.setLeftAnchor(
    this,
    AuthorValue.leftBorder
  )
  AnchorPane.setTopAnchor(
    this,
    AuthorValue.topBorder
  )
}

object AuthorValue {
  private val topBorder: Double = 30.0
  private val leftBorder: Double = 90.0
}
