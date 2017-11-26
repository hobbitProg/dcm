package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import scala.collection.Set

import java.net.URI
import java.util.function.{Consumer, Supplier}

import javafx.application.Application
import javafx.scene.input.{KeyCode, MouseButton}

import org.testfx.api.{FxRobot, FxRobotInterface, FxToolkit}
import org.testfx.matcher.control.TextInputControlMatchers
import org.testfx.util.NodeQueryUtils

import scalafx.Includes._
import scalafx.scene.Scene

import org.scalatest.{BeforeAndAfter, FreeSpec, Matchers}

import org.scalamock.scalatest.MockFactory

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.control.BookDialogParent
import com.github.hobbitProg.dcm.client.books.dialog.{ModifyBookDialog,
  BookEntryDialog}
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.dialog.CategorySelectionDialog
import com.github.hobbitProg.dcm.scalafx.ControlRetriever


/**
  * Specification for modifying book that exists in catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingBookSpec
    extends FreeSpec
    with BeforeAndAfter
    with MockFactory
    with Matchers
    with ControlRetriever {
  class BookData(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  )
  extends Book {
    override def equals(
      that: Any
    ): Boolean = {
      that match {
        case that: BookData =>
          title == that.title &&
          author == that.author &&
          isbn == that.isbn &&
          description == that.description &&
          coverImage == that.coverImage &&
          categories == that.categories
        case _ => false
      }
    }

    override def hashCode: Int = {
      var collectedInfo =
        title +
      author +
      isbn
      description match {
        case Some(descriptionText) =>
          collectedInfo =
            collectedInfo + descriptionText
        case None =>
      }
      coverImage match {
        case Some(coverLocation) =>
          collectedInfo =
            collectedInfo + coverLocation.toString
        case None =>
      }
      collectedInfo =
        categories.foldLeft(
          collectedInfo
        ) {
          (gatheredInfo, currentCategory) =>
          gatheredInfo + currentCategory
        }
      collectedInfo.hashCode
    }
  }

  // Robot to automate entering in information
  private val modifyBookRobot: FxRobotInterface =
    new FxRobot

  // Application being run
  private var runningApp: Application = _

  // Valid new book to add
  val bookImageLocation: URI =
    getClass.getResource(
      "/GroundZero.jpg"
    ).toURI

  after {
    FxToolkit.cleanupStages()
    FxToolkit.cleanupApplication(
      runningApp
    )
  }

  "Given the categories that can be associated with books" - {
    val definedCategories: Set[String] =
      Set[String](
        "sci-fi",
        "conspiracy",
        "fantasy",
        "thriller"
      )

    "and a book that already exists in the catalog" - {
      val originalBook: BookData =
        new BookData(
          "Ruins",
          "Kevin J. Anderson",
          "006105223X",
          Some("Description for Ground Zero"),
          Some[URI](
            bookImageLocation
          ),
          Set[String](
            "sci-fi",
            "conspiracy"
          )
        )

      "and the new title of the book" - {
        val newTitle: Titles =
          "Ground Zero"

        "and the catalog that is being updated" - {
          val catalog: BookCatalog =
            new BookCatalog()

          "and the repository to place book catalog information into" - {
            val repository =
              mock[BookCatalogRepository];

            "and the service for the book catalog" - {
              val service =
                new TestService()

              "and the parent window that created the book modification " +
              "dialog" - {
                val parent =
                  new TestParent(
                    catalog
                  )

                "when the book dialog is created" - {
                  val bookModificationDialog: Scene =
                    createBookModificationDialog(
                      catalog,
                      repository,
                      service,
                      definedCategories,
                      parent,
                      originalBook
                    )

                  "and the title of the book is modified" - {
                    activateControl(
                      BookEntryDialog.titleControlId
                    )
                    clearControl(
                      originalBook.title.length()
                    )
                    enterDataIntoControl(
                      newTitle
                    )

                    "and the book information is saved" - {
                      activateControl(
                        BookEntryDialog.saveButtonId
                      )

                      "then the book entry dialog is closed" in {
                        (bookModificationDialog.window.value == null ||
                          !bookModificationDialog.window.value.showing.value) should be (true)
                      }

                      "and the original book is removed via the service" in pending
                      "and the new book is added via the service" in pending
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
    * Create dialog to modify book that exists in catalog
    *
    * @param catalog Catalog to add new book to
    * @param repository Repository containing book catalog data
    * @param service Service that handles book catalog
    * @param definedCategories Categories available to be associated with book
    * @param parent Parent window that created book addition dialog
    * @param originalBook Book within catalog that is being modified
    * @return Dialog to modify book within catalog
    */
  private def createBookModificationDialog(
    catalog: BookCatalog,
    repository: BookCatalogRepository,
    service: BookCatalogService[BookCatalog],
    definedCategories: Set[String],
    parent: BookDialogParent,
    originalBook: Book
  ): Scene = {
    // Create mock file chooser
    val coverImageChooser: TestChooser =
      new TestChooser(
        bookImageLocation
      )

    FxToolkit.registerPrimaryStage()
    runningApp =
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
      new ModifyBookDialog(
        catalog,
        repository,
        service,
        parent,
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

  // Clear contents of currently selected text control
  def clearControl(
    lengthOfText: Int
  ) = {
    for (characterDeleted <- 1 to lengthOfText) {
      modifyBookRobot push KeyCode.BACK_SPACE
    }
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
