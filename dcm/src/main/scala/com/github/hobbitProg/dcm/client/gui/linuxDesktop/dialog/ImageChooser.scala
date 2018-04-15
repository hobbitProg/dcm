package com.github.hobbitProg.dcm.client.gui.linuxDesktop.dialog

import java.io.File

import scalafx.stage.Window

/**
  * Selects image for catalog entry
  * @author Kyle Cranmer
  * @since 0.2
  */
trait ImageChooser {
  /**
    * Select image for catalog entry
    *
    * @param parentWindow Window that requested image
    *
    * @return Information on file containing image
    */
  def selectImage(
    parentWindow: Window
  ): File
}
