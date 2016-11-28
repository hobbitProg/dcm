package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import java.io.File
import java.util.function.{Consumer, Supplier}
import javafx.application.Application
import javafx.scene.input.{KeyCode, MouseButton}

import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.stage.FileChooser
import org.testfx.api.{FxRobot, FxRobotInterface, FxToolkit}
import org.testfx.util.NodeQueryUtils
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FreeSpec, Matchers}

import scala.collection.Set
import com.github.hobbitProg.dcm.client.dialog.CategorySelectionDialog
import com.github.hobbitProg.dcm.client.books.bookCatalog.{Book, Catalog}
import com.github.hobbitProg.dcm.client.books.bookCatalog.Implicits._
import com.github.hobbitProg.dcm.client.books.dialog.BookEntryDialog

/**
  * Verifies dialog that allows books to be edited can add books to catalog
  */
class BookEntryDialogAllowsUsersToAddBooksToCatalogMultiJvm
  extends FreeSpec
    with MockFactory
    with Matchers {
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
    val catalog: Catalog =
      new TestCatalog
    var addedBook: Book = null
    catalog onAdd {
      newBook =>
        addedBook = newBook
    }

    "and a collection of defined categories" - {
      val definedCategories: Set[String] =
        Set[String](
          "sci-fi",
          "conspiracy",
          "fantasy",
          "thriller"
        )

      "and dialog to fill with details of book to add to catalog" - {
        val bookAdditionDialog: Scene =
          createBookAdditionDialog(
            catalog,
            definedCategories
          )

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
                    activateControl(
                      BookEntryDialog.categorySelectionButtonId
                    )

                    "and the user selects the first category with the new " +
                      "book" - {
                      selectCategory(
                        validNewBook.categories.head
                      )

                      "and the user selects the second category with the new " +
                        "book" - {
                        selectCategory(
                          validNewBook.categories.last
                        )
                        activateControl(
                          CategorySelectionDialog.availableButtonId
                        )
                        activateControl(
                          CategorySelectionDialog.saveButtonId
                        )

                        "when the user accepts the information on the new " +
                          "book" - {
                          activateControl(
                            BookEntryDialog.saveButtonId
                          )

                          "then the dialog is closed" in
                            pending
                          "and the book was added to the catalog" in {
                            addedBook shouldEqual validNewBook
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
  }

  // Test book catalog
  private def bookCatalog: Catalog = {
    val testCatalog =
      mock[Catalog]
    (testCatalog.+ _).expects(
      validNewBook
    ).returning(
      testCatalog
    )
    testCatalog
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
    * Select given category
    * @param category Category to select
    */
  private def selectCategory(
    category: String
  ) = {
    newBookRobot.press(
      KeyCode.CONTROL
    )
    newBookRobot.clickOn(
      NodeQueryUtils hasText category,
      MouseButton.PRIMARY
    )
    newBookRobot.release(
      KeyCode.CONTROL
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
    * @param catalog Catalog to add to
    * @param definedCategories Categories available to be associated with book
    *
    * @return Dialog to add book to catalog
    */
  private def createBookAdditionDialog(
    catalog: Catalog,
    definedCategories: Set[String]
  ): Scene = {
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
        coverImageChooser,
        catalog,
        definedCategories
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
