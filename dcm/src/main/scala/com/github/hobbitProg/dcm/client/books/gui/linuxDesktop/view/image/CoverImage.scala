package com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.view.image

import java.io.{File, FileInputStream}
import java.net.URI

import javafx.scene.layout.{Border, BorderStroke, BorderStrokeStyle,
CornerRadii}
import javafx.scene.paint.Color

import scalafx.Includes._
import scalafx.beans.property.ObjectProperty
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{AnchorPane, VBox}

/**
  * Control that displays cover of book
  * @author Kyle Cranmer
  * @since 0.1
  */
class CoverImage
  extends VBox {
  private val imageControl: ImageView =
    new ImageView
  border =
    new Border(
      new BorderStroke(
        Color.BLACK,
        BorderStrokeStyle.SOLID,
        CornerRadii.EMPTY,
        BorderStroke.THIN
      )
    )
  children =
    List(
      imageControl
    )
  minHeight = CoverImage.imageHeight
  minWidth = CoverImage.imageWidth
  AnchorPane.setLeftAnchor(
    this,
    CoverImage.leftBorder
  )
  AnchorPane.setTopAnchor(
    this,
    CoverImage.topBorder
  )

  /**
    * Set image of book cover
    * @param location Location of book cover image
    */
  def image_=(
    location: URI
  ): Unit = {
    imageControl.image =
      if (location == null)
        null
      else
        new Image(
          new FileInputStream(
            new File(
              location
            )
          ),
          CoverImage.imageWidth,
          CoverImage.imageHeight,
          true,
          true
        )
  }

  /**
    * Retrieve image of book cover
    * @return Image of book cover
    */
  def image: ObjectProperty[javafx.scene.image.Image] =
    imageControl.image
}

object CoverImage {
  private val leftBorder: Double = 2.0
  private val topBorder: Double = 204.0

  private val imageWidth: Double = 160.0
  private val imageHeight: Double = 160.0
}
