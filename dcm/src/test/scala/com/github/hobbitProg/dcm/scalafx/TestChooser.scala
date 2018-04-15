package com.github.hobbitProg.dcm.scalafx

import java.io.File
import java.net.URI

import scalafx.stage.Window

import com.github.hobbitProg.dcm.client.gui.linuxDesktop.dialog.ImageChooser

/**
  * Image chooser to test book catalog dialogs
  */
class TestChooser(
  private val imageLocation: URI
) extends ImageChooser {
  /**
    * Select image for catalog entry
    *
    * @param parentWindow Window that requested image
    *
    * @return Information on file containing image
    */
  def selectImage(
    parentWindow: Window
  ): File = {
    new File(
      imageLocation
    )
  }
}
