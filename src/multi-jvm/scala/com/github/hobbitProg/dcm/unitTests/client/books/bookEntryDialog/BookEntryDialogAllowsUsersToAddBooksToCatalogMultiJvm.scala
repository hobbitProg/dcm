package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import java.io.File
import java.util.function.{Consumer, Supplier}
import javafx.application.Application
import javafx.scene.input.{KeyCode, MouseButton}
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.Stage

import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.stage.{FileChooser, Stage}
import scalafx.stage.Stage._
import org.testfx.api.{FxRobot, FxRobotInterface, FxToolkit}
import org.testfx.util.NodeQueryUtils
import org.scalamock.scalatest.MockFactory
import org.scalatest.FreeSpec

import scala.collection.Set
import com.github.hobbitProg.dcm.client.books.bookCatalog.Book
import com.github.hobbitProg.dcm.client.books.bookCatalog.Implicits._
import com.github.hobbitProg.dcm.client.books.dialog.BookEntryDialog

/**
  * Verifies dialog that allows books to be edited can add books to catalog
  */
class BookEntryDialogAllowsUsersToAddBooksToCatalogMultiJvm
  extends FreeSpec
    with MockFactory {
  // Robot to automate entering in information
  val newBookRobot: FxRobotInterface =
    new FxRobot

  // Valid new book to add
  private val validNewBook: Book =
    ("Ground Zero",
      "Kevin J. Anderson",
      "006105223X",
      "Description for Ground Zero",
      "GroundZero.jpg",
      Set[String](
        "sci-fi",
        "conspiracy"
      ))

  "Given a book catalog" - {
    "and a collection of defined categories" - {
      "and dialog to fill with details of book to add to catalog" - {
        val bookAdditionDialog: Scene =
          BookAdditionDialog

        "when the user enters the title of the new book" - {
          activateControl(
            BookEntryDialog.titleControlId
          )
          enterDataIntoControl(
            validNewBook.title
          )

          "and the user enters the author of the new book" - {
            activateControl(
              BookEntryDialog.authorControlId
            )
            enterDataIntoControl(
              validNewBook.author
            )

            "and the user enters the ISBN of the new book" - {
              activateControl(
                BookEntryDialog.isbnControlId
              )
              enterDataIntoControl(
                validNewBook.isbn
              )

              "and the user enters the description of the new book" - {
                activateControl(
                  BookEntryDialog.descriptionControlId
                )
                enterDataIntoControl(
                  validNewBook.description
                )

                "and the user selects the cover image for the new book" - {
                  activateControl(
                    BookEntryDialog.bookCoverButtonId
                  )

                  "and the user requests to associate categories with the new" +
                    " book" - {
                    "and the user selects the first category with the new " +
                      "book" - {
                      "and the user selects the second category with the new " +
                        "book" - {
                        "when the user accepts the information on the new " +
                          "book" - {
                          "then the dialog is closed" in
                            pending
                          "and the book was added to the catalog" in
                            pending
                          "and the original contents of the catalog still " +
                            "exist in the catalog" in
                            pending
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  /**
    * Activate control to edit
    * @param controlId ID of control to activate
    */
  private def activateControl(
    controlId: String
  ) = {
    newBookRobot.clickOn(
      NodeQueryUtils hasId controlId,
      MouseButton.PRIMARY
    )
  }

  /**
    * Enter data into currently active control
    * @param dataToEnter Data to place into control
    */
  private def enterDataIntoControl(
    dataToEnter: String
  ) = {
    dataToEnter.toCharArray foreach {
      case current@upperCase if current.isLetter && current.isUpper =>
        newBookRobot push(
          KeyCode.SHIFT,
          KeyCode getKeyCode upperCase.toString
        )
      case current@space if current == ' ' =>
        newBookRobot push KeyCode.SPACE
      case current@period if current == '.' =>
        newBookRobot push KeyCode.PERIOD
      case current =>
        newBookRobot push (KeyCode getKeyCode current.toUpper.toString)
    }
  }

  /**
    * Create dialog to add book to catalog
    *
    * @return Dialog to add book to catalog
    */
  private def BookAdditionDialog: Scene = {
    // Create mock file choolser
    val coverImageChooser =
      mock[FileChooser]

    // Create test application
    FxToolkit.registerPrimaryStage()
    FxToolkit.setupApplication(
      new Supplier[Application] {
        override def get(): BookEntryUnitTestApplication = {
          new BookEntryUnitTestApplication
        }
      }
    )
    FxToolkit.showStage()

    // Create dialog to add book to catalog
    val bookEntryDialog =
      new BookEntryDialog(
        coverImageChooser
      )
    val bookEntryStage: javafx.stage.Stage =
      FxToolkit.setupStage(
        new Consumer[javafx.stage.Stage] {
          override def accept(t: javafx.stage.Stage): Unit = {
            t.scene = bookEntryDialog
          }
        }
      )

    // Create expectations for mock file chooser
    (coverImageChooser.showOpenDialog _).expects(
      jfxStage2sfx(
        bookEntryStage
      )
    ).returning(
      new File(
        getClass.getResource(
          "/" +
          validNewBook.coverImage
        ).toURI
      )
    )

    bookEntryDialog
  }
}
