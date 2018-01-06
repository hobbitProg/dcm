package com.github.hobbitProg.dcm.scalafx

import java.net.URI
import java.util.function.{Consumer, Supplier}

import scala.collection.Set

import org.testfx.api.{FxRobot, FxRobotInterface, FxToolkit}
import org.testfx.util.NodeQueryUtils

import javafx.application.Application
import javafx.scene.input.{KeyCode, MouseButton}

import scalafx.Includes._
import scalafx.scene.Scene

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.BookCatalog
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.books.control.BookDialogParent
import com.github.hobbitProg.dcm.client.books.dialog.BookEntryDialog
import com.github.hobbitProg.dcm.client.dialog.ImageChooser
import com.github.hobbitProg.dcm.matchers.scalafx.ListViewCellMatcher

/**
  * Helper routines for verifying book entry dialog
  * @author Kyle Cranmer
  * @since 0.2
  */
trait BookDialogHelper {
  var runningApp: Application = _

  val bookImageLocation: URI =
    getClass.getResource(
      "/GroundZero.jpg"
    ).toURI

  // Robot to automate entering in information
  val bookDialogRobot: FxRobotInterface =
    new FxRobot

  /**
    * Create dialog that is to be tested
    *
    * @param catalog Catalog to add new book to
    * @param repository Repository containing book catalog data
    * @param service Service that handles book catalog
    * @param parent Parent window that created book addition dialog
    * @param coverImageChooser Dialog to choose image for cover
    * @param definedCategories Categories available to be associated with book
    *
    * @return Dialog to be tested
    */
  def createDialog(
    catalog: BookCatalog,
    repository: BookCatalogRepository,
    service: BookCatalogService[BookCatalog],
    parent: BookDialogParent,
    coverImageChooser: ImageChooser,
    definedCategories: Set[String]
  ): BookEntryDialog

  /**
    * Create dialog to edit book
    *
    * @param catalog Catalog to add new book to
    * @param repository Repository containing book catalog data
    * @param service Service that handles book catalog
    * @param definedCategories Categories available to be associated with book
    * @param parent Parent window that created book addition dialog
    *
    * @return Dialog to edit book
    */
  def createBookDialog(
    catalog: BookCatalog,
    repository: BookCatalogRepository,
    service: BookCatalogService[BookCatalog],
    definedCategories: Set[String],
    parent: BookDialogParent,
    coverLocation: URI = bookImageLocation
  ): Scene = {
    // Create mock file chooser
    val coverImageChooser: TestChooser =
      new TestChooser(
        coverLocation
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
      createDialog(
        catalog,
        repository,
        service,
        parent,
        coverImageChooser,
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

    bookEntryDialog
  }

  /**
    * Activate control to edit
    * @param controlId ID of control to activate
    */
  def activateControl(
    controlId: String
  ) = {
    bookDialogRobot.clickOn(
      NodeQueryUtils hasId controlId,
      MouseButton.PRIMARY
    )
  }

  /**
    * Enter data into currently active control
    * @param dataToEnter Data to place into control
    */
  def enterDataIntoControl(
    dataToEnter: String
  ) = {
    dataToEnter.toCharArray foreach {
      case current@upperCase if current.isLetter && current.isUpper =>
        bookDialogRobot push(
          KeyCode.SHIFT,
          KeyCode getKeyCode upperCase.toString
        )
      case current@space if current == ' ' =>
        bookDialogRobot push KeyCode.SPACE
      case current@period if current == '.' =>
        bookDialogRobot push KeyCode.PERIOD
      case current =>
        bookDialogRobot push (KeyCode getKeyCode current.toUpper.toString)
    }
  }

  /**
    * Select given category
    * @param category Category to select
    * @param viewId The ID of the list view that contains the cell to click
    */
  def selectCategory(
    category: String,
    viewId: String
  ) = {
    bookDialogRobot.press(
      KeyCode.CONTROL
    )
    bookDialogRobot.clickOn(
      ListViewCellMatcher.hasText(
        viewId,
        category
      ),
      MouseButton.PRIMARY
    )
    bookDialogRobot.release(
      KeyCode.CONTROL
    )
  }

  // Clear contents of currently selected text control
  def clearControl(
    lengthOfText: Int
  ) = {
    bookDialogRobot push KeyCode.END
    for (characterDeleted <- 1 to lengthOfText) {
      bookDialogRobot push KeyCode.BACK_SPACE
    }
  }
}
