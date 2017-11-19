package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import java.io.File
import java.net.URI
import java.util.function.{Consumer, Supplier}

import javafx.application.Application
import javafx.scene.input.{KeyCode, MouseButton}

import org.testfx.api.{FxRobot, FxRobotInterface, FxToolkit}
import org.testfx.util.NodeQueryUtils

import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.stage.FileChooser

import org.specs2.mutable.Specification

import org.scalamock.specs2.MockContext

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.control.BookDialogParent
import com.github.hobbitProg.dcm.client.books.dialog.{AddBookDialog, BookEntryDialog}
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.dialog.CategorySelectionDialog

/**
  * Specification for adding book to the catalog using dialog
  * @author Kyle Cranmer
  * @since 0.2
  */
class AddingBookSpec
    extends Specification {
  class BookData(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) {
  }

  // Robot to automate entering in information
  val newBookRobot: FxRobotInterface =
    new FxRobot

  // Valid new book to add
  val bookImageLocation: URI =
    getClass.getResource(
      "/GroundZero.jpg"
    ).toURI

  "When the user enters data on a book that does not exist in the catalog nor " +
  "the repository, when the data is accepted"  >> {
    "the book entry dialog is closed" >> new MockContext {
      val definedCategories: Set[String] =
        Set[String](
          "sci-fi",
          "conspiracy",
          "fantasy",
          "thriller"
        )
      val validNewBook: BookData =
        new BookData(
          "Ground Zero",
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
      val catalog: BookCatalog =
        new BookCatalog()
      val repository =
        mock[BookCatalogRepository];
      val service =
        new TestService()
      val parent =
        new TestParent(
          catalog
        )

      val bookAdditionDialog: Scene =
        createBookAdditionDialog(
          catalog,
          repository,
          service,
          definedCategories,
          parent
        )
      
      activateControl(
        BookEntryDialog.titleControlId
      )
      enterDataIntoControl(
        validNewBook.title
      )

      activateControl(
        BookEntryDialog.authorControlId
      )
      enterDataIntoControl(
        validNewBook.author
      )

      activateControl(
        BookEntryDialog.isbnControlId
      )
      enterDataIntoControl(
        validNewBook.isbn
      )

      activateControl(
        BookEntryDialog.descriptionControlId
      )
      enterDataIntoControl(
        validNewBook.description match {
          case Some(existingDescription) => existingDescription
          case None => ""
        }
      )

      activateControl(
        BookEntryDialog.bookCoverButtonId
      )

      activateControl(
        BookEntryDialog.categorySelectionButtonId
      )
      selectCategory(
        validNewBook.categories.head
      )
      selectCategory(
        validNewBook.categories.last
      )
      activateControl(
        CategorySelectionDialog.availableButtonId
      )
      activateControl(
        CategorySelectionDialog.saveButtonId
      )
      activateControl(
        BookEntryDialog.saveButtonId
      )

      (bookAdditionDialog.window.value == null ||
        !bookAdditionDialog.window.value.showing.value) must beTrue
    }

    "the book is placed into the catalog" >> pending
    "the book is placed into the repository" >> pending
  }

  "When the user enters data on a book (except the title)" >> {
    "the user cannot accept the data" >> pending
  }

  "When the user enters data on a book (except the author)" >> {
    "the user cannot accept the data" >> pending
  }

  "When the user enters data on a book (except the ISBN)" >> {
    "the user cannot accept the data" >> pending
  }

  "When the user enters data on a book (with a title and author that already " +
  "exists in the catalog" >> {
    "the user cannot accept the data" >> pending
  }

  "When the user enters data on a book (with a title and author that already " +
  "exists in the repository" >> {
    "the user cannot accept the data" >> pending
  }

  "When the user enters data on a book (with an ISBN that already exists in " +
  "the catalog)" >> {
    "the user cannot accept the data" >> pending
  }

  "When the user enters data on a book (with an ISBN that already exists in " +
  "the repository)" >> {
    "the user cannot accept the data" >> pending
  }

  /**
    * Create dialog to add book to catalog
    *
    * @param catalog Catalog to add new book to
    * @param repository Repository containing book catalog data
    * @param service Service that handles book catalog
    * @param definedCategories Categories available to be associated with book
    * @param parent Parent window that created book addition dialog
    *
    * @return Dialog to add book to catalog
    */
  private def createBookAdditionDialog(
    catalog: BookCatalog,
    repository: BookCatalogRepository,
    service: BookCatalogService[BookCatalog],
    definedCategories: Set[String],
    parent: BookDialogParent
  ): Scene = {
    // Create mock file chooser
    val coverImageChooser: TestChooser =
      new TestChooser(
        bookImageLocation
      )

    // Create test application
    FxToolkit.registerPrimaryStage()
    FxToolkit.setupApplication(
      new Supplier[Application] {
        override def get(): BookEntryUnitTestApplication = {
          new BookEntryUnitTestApplication()
        }
      }
    )
    FxToolkit.showStage()

    // Create dialog to add book to catalog
    val bookEntryDialog =
      new AddBookDialog(
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
}
