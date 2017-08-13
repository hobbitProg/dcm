package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import java.io.File
import java.net.URI
import java.util.function.{Consumer, Supplier}

import javafx.application.Application
import javafx.scene.input.{KeyCode, MouseButton}

import org.scalamock.scalatest.MockFactory

import org.scalatest.{FreeSpec, Matchers}

import scala.collection.Set

import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.stage.FileChooser

import org.testfx.api.{FxRobot, FxRobotInterface, FxToolkit}
import org.testfx.util.NodeQueryUtils

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.BookCatalog
import com.github.hobbitProg.dcm.client.books.dialog.{ModifyBookDialog, BookEntryDialog}

class BookEntryDialogAllowsUsersToModifyBooksWithinCatalog
    extends FreeSpec
    with MockFactory
    with Matchers {
  private case class TestBook(
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    coverImage: CoverImages,
    categories: Set[Categories]
  ) extends Book {
  }

  // New information for book
  private val updatedTitle: Titles =
    "Goblins"
  // Valid new book to add
  private val bookImageLocation: URI =
    getClass.getResource(
      "/GroundZero.jpg"
    ).toURI

  // Robot to automate entering in information
  val modifyBookRobot: FxRobotInterface =
    new FxRobot

  "Given a book catalog" - {
    val catalog =
      new TestCatalog()

    "and a populated repository for the book catalog" - {
      val repository =
        mock[BookRepository]

      "and a book within the repository to modify" - {
        val originalBook =
          TestBook(
            "Ruins",
            "Charles Grant",
            "0061054143",
            Some(
              "Description for Goblins"
            ),
            Some(
              getClass.getResource(
                "/Goblins.jpg"
              ).toURI
            ),
            Set[Categories](
              "sci-fi",
              "conspiracy"
            )
          )

        "and a collection of defined categories" - {
        val definedCategories: Set[Categories] =
          Set[Categories](
            "sci-fi",
            "conspiracy",
            "fantasy",
            "thriller"
          )

          "and dialog to change the details of the given book" - {
            val bookModificationDialog: Scene =
              createBookModificationDialog(
                originalBook,
                catalog,
                repository,
                definedCategories
              )

            "when the user changes the title of the book to a title not in " +
            "the catalog" - {
              activateControl(
                BookEntryDialog.titleControlId
              )
              clearControl(
                originalBook.title.length
              )
              enterDataIntoControl(
                updatedTitle
              )

              "and the user accpets the updated information" - {
                activateControl(
                  BookEntryDialog.saveButtonId
                )

                "then the dialog is closed" in {
                  (bookModificationDialog.window.value == null ||
                    !bookModificationDialog.window.value.showing.value) should be (true)
                }


                "and the updated book was added to the catalog" in {
                  catalog.newTitle should be (updatedTitle)
                  catalog.newAuthor should be (originalBook.author)
                  catalog.newISBN should be (originalBook.isbn)
                  catalog.newDescription should be (originalBook.description)
                  catalog.newCover should be (originalBook.coverImage)
                  catalog.newCategories should be (originalBook.categories)
                }

                "and the original book was removed from the catalog" in {
                  catalog.removedISBN should be (originalBook.isbn)
                }
              }
            }
          }
        }
      }
    }
  }

  /**
    * Create dialog to modify book in catalog
    *
    * @param originalBook Book already in catalog
    * @param catalog Catalog being worked on
    * @param repository Repository for book category
    * @param definedCategories Categories available to be associated with book
    *
    * @return Dialog to modify book within catalog
    */
  private def createBookModificationDialog(
    originalBook: Book,
    catalog: BookCatalog,
    repository: BookRepository,
    definedCategories: Set[Categories]
  ): Scene = {
  // Create mock file chooser
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

    // Create dialog to modify book within catalog
    val bookEntryDialog =
      new ModifyBookDialog(
        catalog,
        repository,
        coverImageChooser,
        definedCategories,
        originalBook
      )

    val bookEntryStage: javafx.stage.Stage =
      FxToolkit.setupStage(
        new Consumer[javafx.stage.Stage] {
          override def accept(t: javafx.stage.Stage): Unit = {
            t.scene = bookEntryDialog
          }
        }
      )

    bookEntryDialog
  }

  /**
    * Activate control to edit
    * @param controlId ID of control to activate
    */
  private def activateControl(
    controlId: String
  ) = {
    modifyBookRobot.clickOn(
      NodeQueryUtils hasId controlId,
      MouseButton.PRIMARY
    )
  }

  /**
    * Erase data from currently active control
    * @param dataLength Amount of data to erase from control
    */
  private def clearControl(
    dataLength: Int
  ) = {
    modifyBookRobot eraseText dataLength
  }

  /**
    * Enter data into currently active control
    * @param dataToEnter Data to place into control
    */
  private def enterDataIntoControl(
    dataToEnter: String
  ) = {
    //noinspection ScalaUnusedSymbol,ScalaUnusedSymbol
    dataToEnter.toCharArray foreach {
      case current@upperCase if current.isLetter && current.isUpper =>
        modifyBookRobot push(
          KeyCode.SHIFT,
          KeyCode getKeyCode upperCase.toString
        )
      case current@space if current == ' ' =>
        modifyBookRobot push KeyCode.SPACE
      case current@period if current == '.' =>
        modifyBookRobot push KeyCode.PERIOD
      case current =>
        modifyBookRobot push (KeyCode getKeyCode current.toUpper.toString)
    }
  }
}
