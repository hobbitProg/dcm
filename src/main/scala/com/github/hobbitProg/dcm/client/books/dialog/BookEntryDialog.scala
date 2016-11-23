package com.github.hobbitProg.dcm.client.books.dialog

import scala.collection.Seq
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.{Label, TextField}

/**
  * Dialog for entering information on book for catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookEntryDialog
  extends Scene {

  // Create control for entering in title of book
  private val titleControl: TextField =
    new TextField
  titleControl.id = BookEntryDialog.titleControlId
  content =
    Seq[Node](
      new Label("Title:"),
      titleControl
    )
}

object BookEntryDialog {
  val titleControlId = "bookTitleControl"
}
