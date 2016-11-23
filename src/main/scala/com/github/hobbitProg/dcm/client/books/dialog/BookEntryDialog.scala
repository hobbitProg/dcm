package com.github.hobbitProg.dcm.client.books.dialog

import javafx.event.EventHandler
import javafx.scene.input.InputMethodEvent

import scala.collection.Seq
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.{Label, TextField}
import com.github.hobbitProg.dcm.client.books.bookCatalog.Book

/**
  * Dialog for entering information on book for catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookEntryDialog
  extends Scene {

  // Book being edited
  private val bookBeingEdited =
    new Book

  // Create control for entering in title of book
  private val titleControl: TextField =
    new TextField
  titleControl.id = BookEntryDialog.titleControlId
  titleControl.text.onChange {
    bookBeingEdited.title = titleControl.text.value
  }
  content =
    Seq[Node](
      new Label("Title:"),
      titleControl
    )
}

object BookEntryDialog {
  val titleControlId = "bookTitleControl"
}
