package com.github.hobbitProg.dcm.integrationTests.client.books

import java.io.File
import java.util.function.{Consumer, Predicate, Supplier}

import scala.collection.{JavaConverters, Set}
import scala.util.Success
import JavaConverters._

import doobie._, doobie.implicits._

import cats._, cats.data._, cats.effect._, cats.implicits._

import javafx.application.Application
import javafx.scene.input.{KeyCode, MouseButton}

import scalafx.Includes._
import scalafx.scene.control.{ListView, TextInputControl}
import scalafx.scene.layout.{AnchorPane, VBox}
import scalafx.stage.Window

import org.scalamock.scalatest.MockFactory

import org.scalatest.{FeatureSpec, GivenWhenThen, BeforeAndAfter, Matchers}

import org.testfx.api.{FxRobot, FxRobotContext, FxRobotInterface, FxToolkit}
import org.testfx.util.{NodeQueryUtils, WaitForAsyncUtils}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.
  BookCatalogServiceInterpreter
import com.github.hobbitProg.dcm.client.books.dialog.BookEntryDialog
import com.github.hobbitProg.dcm.client.dialog.{CategorySelectionDialog,
  ImageChooser}
import com.github.hobbitProg.dcm.client.control.BookTabControl
import com.github.hobbitProg.dcm.client.linuxDesktop.{BookTab, DCMDesktop}
import com.github.hobbitProg.dcm.integrationTests.matchers.scalaTest.
  {IntegrationMatchers, ScalafxMatchers}
import IntegrationMatchers._
import ScalafxMatchers._

/**
  * Specification for adding a book to the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class AddBookSpec
    extends FeatureSpec
    with GivenWhenThen
    with BeforeAndAfter
    with Matchers
    with IntegrationMatchers
    with MockFactory
    with BookDBAccess
    with GUIAutomation {

  // Chooses cover of book
  private val coverChooser: ImageChooser = mock[ImageChooser]

  before {
    createBookCatalogSchema()
  }

  after {
    // Remove database file
    removeDatabaseFile()

    // Shut down windows
    shutDownApplication()
  }

  /**
    * Select given category
    * @param category Category to select
    */
  private def selectCategory(
    category: Categories
  ) = {
    bookClientRobot.press(
      KeyCode.CONTROL
    )
    bookClientRobot.clickOn(
      NodeQueryUtils hasText category,
      MouseButton.PRIMARY
    )
    bookClientRobot.release(
      KeyCode.CONTROL
    )
  }

  // Find book entry dialog
  def findBookEntryDialog: Window = {
    val context =
      new FxRobotContext
    val bookEntryDialogPredicate: java.util.function.Predicate[javafx.stage.Window] =
      (currentWindow: javafx.stage.Window) => {
        val convertedWindow: scalafx.stage.Stage = currentWindow.asInstanceOf[javafx.stage.Stage]
        convertedWindow.title.value == "Add Book To Catalog"
      }
    context.getWindowFinder.window(
      bookEntryDialogPredicate
    )
  }

  Feature("The user can add a book to the book catalog") {
    info("As someone who wants to keep track of books he owns")
    info("I want to add books to the book catalog")
    info("So that I can know what books I own")

    Scenario("A book with all required fields can be added to the book " +
      "catalog.") {
      Given("the pre-defined categories")
      placePreDefinedCategoriesIntoDatabase()

      And("a populated catalog")
      placeExistingBooksIntoDatabase()
      val catalog: BookCatalog =
        new BookCatalog()

      showMainApplication(
        catalog,
        bookTransactor,
        coverChooser
      )

      And("a book to add to the catalog")
      val bookToEnter: Book =
        new BookDBAccess.TestBook(
          "Ground Zero",
          "Kevin J. Anderson",
          "006105223X",
          Some(
            "Description for Ground Zero"
          ),
          Some(
            getClass.getResource(
              "/GroundZero.jpg"
            ).toURI()
          ),
          Set[Categories](
            "sci-fi",
            "conspiracy"
          )
        )

      When("the information on the book is entered")
      // Display dialog to enter in new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookTab.addButtonId,
        MouseButton.PRIMARY
      )

      // Enter in title of new book
      selectTitle()
      enterDataIntoControl(
        bookToEnter.title
      )

      // Enter in author of new book
      selectAuthor()
      enterDataIntoControl(
        bookToEnter.author
      )

      // Enter in ISBN of new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.isbnControlId,
        MouseButton.PRIMARY
      )
      enterDataIntoControl(
        bookToEnter.isbn
      )

      // Enter in description of new book
      bookToEnter.description match {
        case Some(description) =>
          bookClientRobot.clickOn(
            NodeQueryUtils hasId BookEntryDialog.descriptionControlId,
            MouseButton.PRIMARY
          )
          enterDataIntoControl(
            description
          )
        case None =>
      }

      // Select cover of new book
      bookToEnter.coverImage match {
        case Some(coverName) =>
          val dialogStage =
            bookClientRobot.listWindows().asScala.find {
              case possibleDialog: javafx.stage.Stage =>
                possibleDialog.getTitle == BookTabControl.addBookTitle
              case _ => false
            }
          dialogStage match {
            case Some(actualStage) =>
              val adaptedStage: scalafx.stage.Window =
                actualStage
                  (coverChooser.selectImage _).expects(
                    adaptedStage
                  ).returning(
                    new File(
                      coverName
                    )
                  )
            case None =>
          }
          bookClientRobot.clickOn(
            NodeQueryUtils hasId BookEntryDialog.bookCoverButtonId,
            MouseButton.PRIMARY
          )
        case None =>
      }

      // Select categories for new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.categorySelectionButtonId,
        MouseButton.PRIMARY
      )
      for (bookCategory <- bookToEnter.categories) {
        selectCategory(
          bookCategory
        )
      }
      bookClientRobot.clickOn(
        NodeQueryUtils hasId CategorySelectionDialog.availableButtonId,
        MouseButton.PRIMARY
      )
      bookClientRobot.clickOn(
        NodeQueryUtils hasId CategorySelectionDialog.saveButtonId,
        MouseButton.PRIMARY
      )

      And("the information is accepted")
      acceptBookInformation()

      Then("the book is in the catalog")
      getByISBN(
        desktop.bookDisplay.catalog,
        bookToEnter.isbn
      ) should beInCatalog(bookToEnter)

      And("the book is in the repository")
      import BookCatalogRepositoryInterpreter._
      retrieve(
        bookToEnter.isbn
      ) should beInRepository(bookToEnter)

      And("the book is displayed on the view displaying the book catalog")
      bookToEnter should beOn(desktop)

      And("the books that were originally on the view displaying the book " +
        "catalog are still on that window")
      BookDBAccess.existingBooks should allBeOn(desktop)

      And("no books are selected on the window displaying the book catalog")
      desktop should haveNoBooksSelected()

      And("the window displaying the information on the selected book is empty")
      desktop should notHaveSelectedBookDataDisplayed()
    }

    Scenario("A book that does not have a title cannot be added to the book " +
      "catalog") {
      Given("the pre-defined categories")
      placePreDefinedCategoriesIntoDatabase()

      And("a populated catalog")
      placeExistingBooksIntoDatabase()
      val catalog: BookCatalog =
        new BookCatalog()

      showMainApplication(
        catalog,
        bookTransactor,
        coverChooser
      )

      And("the information on the book without a title")
      val bookToEnter: Book =
        new BookDBAccess.TestBook(
          "",
          "Kevin J. Anderson",
          "006105223X",
          Some(
            "Description for Ground Zero"
          ),
          Some(
            getClass.getResource(
              "/GroundZero.jpg"
            ).toURI()
          ),
          Set[Categories](
            "sci-fi",
            "conspiracy"
          )
        )

      When("the information on the book is entered")
      // Display dialog to enter in new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookTab.addButtonId,
        MouseButton.PRIMARY
      )

      // Enter in author of new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.authorControlId,
        MouseButton.PRIMARY
      )
      enterDataIntoControl(
        bookToEnter.author
      )

      // Enter in ISBN of new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.isbnControlId,
        MouseButton.PRIMARY
      )
      enterDataIntoControl(
        bookToEnter.isbn
      )

      // Enter in description of new book
      bookToEnter.description match {
        case Some(description) =>
          bookClientRobot.clickOn(
            NodeQueryUtils hasId BookEntryDialog.descriptionControlId,
            MouseButton.PRIMARY
          )
          enterDataIntoControl(
            description
          )
        case None =>
      }

      // Select cover of new book
      bookToEnter.coverImage match {
        case Some(coverName) =>
          val dialogStage =
            bookClientRobot.listWindows().asScala.find {
              case possibleDialog: javafx.stage.Stage =>
                possibleDialog.getTitle == BookTabControl.addBookTitle
              case _ => false
            }

          dialogStage match {
            case Some(actualStage) =>
              val adaptedStage: scalafx.stage.Window =
                actualStage
                  (coverChooser.selectImage _).expects(
                    adaptedStage
                  ).returning(
                    new File(
                      coverName
                    )
                  )
            case None =>
          }
          bookClientRobot.clickOn(
            NodeQueryUtils hasId BookEntryDialog.bookCoverButtonId,
            MouseButton.PRIMARY
          )
        case None =>
      }

      // Select categories for new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.categorySelectionButtonId,
        MouseButton.PRIMARY
      )
      for (bookCategory <- bookToEnter.categories) {
        selectCategory(
          bookCategory
        )
      }
      bookClientRobot.clickOn(
        NodeQueryUtils hasId CategorySelectionDialog.availableButtonId,
        MouseButton.PRIMARY
      )
      bookClientRobot.clickOn(
        NodeQueryUtils hasId CategorySelectionDialog.saveButtonId,
        MouseButton.PRIMARY
      )

      Then("the information on the book cannot be accepted")
      findBookEntryDialog should haveInactiveSaveButton()
    }

    Scenario("A book that does not have an author cannot be added to the " +
      "book catalog") {
      Given("the pre-defined categories")
      placePreDefinedCategoriesIntoDatabase()

      And("a populated catalog")
      placeExistingBooksIntoDatabase()
      val catalog: BookCatalog =
        new BookCatalog()

      showMainApplication(
        catalog,
        bookTransactor,
        coverChooser
      )

      And("the information on the book without an author")
      val bookToEnter: Book =
        new BookDBAccess.TestBook(
          "Ground Zero",
          "",
          "006105223X",
          Some(
            "Description for Ground Zero"
          ),
          Some(
            getClass.getResource(
              "/GroundZero.jpg"
            ).toURI()
          ),
          Set[Categories](
            "sci-fi",
            "conspiracy"
          )
        )

      When("the information on the book is entered")
      // Display dialog to enter in new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookTab.addButtonId,
        MouseButton.PRIMARY
      )

      // Enter in title of new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.titleControlId,
        MouseButton.PRIMARY
      )
      enterDataIntoControl(
        bookToEnter.title
      )

      // Enter in ISBN of new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.isbnControlId,
        MouseButton.PRIMARY
      )
      enterDataIntoControl(
        bookToEnter.isbn
      )

      // Enter in description of new book
      bookToEnter.description match {
        case Some(description) =>
          bookClientRobot.clickOn(
            NodeQueryUtils hasId BookEntryDialog.descriptionControlId,
            MouseButton.PRIMARY
          )
          enterDataIntoControl(
            description
          )
        case None =>
      }

      // Select cover of new book
      bookToEnter.coverImage match {
        case Some(coverName) =>
          val dialogStage =
            bookClientRobot.listWindows().asScala.find {
              case possibleDialog: javafx.stage.Stage =>
                possibleDialog.getTitle == BookTabControl.addBookTitle
              case _ => false
            }
          dialogStage match {
            case Some(actualStage) =>
              val adaptedStage: scalafx.stage.Window =
                actualStage
                  (coverChooser.selectImage _).expects(
                    adaptedStage
                  ).returning(
                    new File(
                      coverName
                    )
                  )
            case None =>
          }
          bookClientRobot.clickOn(
            NodeQueryUtils hasId BookEntryDialog.bookCoverButtonId,
            MouseButton.PRIMARY
          )
        case None =>
      }

      // Select categories for new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.categorySelectionButtonId,
        MouseButton.PRIMARY
      )
      for (bookCategory <- bookToEnter.categories) {
        selectCategory(
          bookCategory
        )
      }
      bookClientRobot.clickOn(
        NodeQueryUtils hasId CategorySelectionDialog.availableButtonId,
        MouseButton.PRIMARY
      )
      bookClientRobot.clickOn(
        NodeQueryUtils hasId CategorySelectionDialog.saveButtonId,
        MouseButton.PRIMARY
      )

      Then("the information on the book cannot be accepted")
      findBookEntryDialog should haveInactiveSaveButton()
    }

    Scenario("A book that does not have an ISBN cannot be added to the book " +
      "catalog") {
      Given("the pre-defined categories")
      placePreDefinedCategoriesIntoDatabase()

      And("a populated catalog")
      placeExistingBooksIntoDatabase()
      val catalog: BookCatalog =
        new BookCatalog()

      showMainApplication(
        catalog,
        bookTransactor,
        coverChooser
      )

      And("the information on the book without an ISBN")
      val bookToEnter: Book =
        new BookDBAccess.TestBook(
          "Ground Zero",
          "Kevin J. Anderson",
          "",
          Some(
            "Description for Ground Zero"
          ),
          Some(
            getClass.getResource(
              "/GroundZero.jpg"
            ).toURI()
          ),
          Set[Categories](
            "sci-fi",
            "conspiracy"
          )
        )

      When("the information on the book is entered")
      // Display dialog to enter in new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookTab.addButtonId,
        MouseButton.PRIMARY
      )

      // Enter in title of new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.titleControlId,
        MouseButton.PRIMARY
      )
      enterDataIntoControl(
        bookToEnter.title
      )

      // Enter in author of new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.authorControlId,
        MouseButton.PRIMARY
      )
      enterDataIntoControl(
        bookToEnter.author
      )

      // Enter in description of new book
      bookToEnter.description match {
        case Some(description) =>
          bookClientRobot.clickOn(
            NodeQueryUtils hasId BookEntryDialog.descriptionControlId,
            MouseButton.PRIMARY
          )
          enterDataIntoControl(
            description
          )
        case None =>
      }

      // Select cover of new book
      bookToEnter.coverImage match {
        case Some(coverName) =>
          val dialogStage =
            bookClientRobot.listWindows().asScala.find {
              case possibleDialog: javafx.stage.Stage =>
                possibleDialog.getTitle == BookTabControl.addBookTitle
              case _ => false
            }
          dialogStage match {
            case Some(actualStage) =>
              val adaptedStage: scalafx.stage.Window =
                actualStage
                  (coverChooser.selectImage _).expects(
                    adaptedStage
                  ).returning(
                    new File(
                      coverName
                    )
                  )
            case None =>
          }
          bookClientRobot.clickOn(
            NodeQueryUtils hasId BookEntryDialog.bookCoverButtonId,
            MouseButton.PRIMARY
          )
        case None =>
      }

      // Select categories for new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.categorySelectionButtonId,
        MouseButton.PRIMARY
      )
      for (bookCategory <- bookToEnter.categories) {
        selectCategory(
          bookCategory
        )
      }
      bookClientRobot.clickOn(
        NodeQueryUtils hasId CategorySelectionDialog.availableButtonId,
        MouseButton.PRIMARY
      )
      bookClientRobot.clickOn(
        NodeQueryUtils hasId CategorySelectionDialog.saveButtonId,
        MouseButton.PRIMARY
      )

      Then("the information on the book cannot be accepted")
      findBookEntryDialog should haveInactiveSaveButton()
    }

    Scenario("A book that has a title/author pair that already exists within " +
      "the book catalog cannot be added to the catalog") {
      Given("the pre-defined categories")
      placePreDefinedCategoriesIntoDatabase()

      And("a populated catalog")
      placeExistingBooksIntoDatabase()
      val catalog: BookCatalog =
        new BookCatalog()

      showMainApplication(
        catalog,
        bookTransactor,
        coverChooser
      )

      And("the information on the book with a duplicate title/author pair")
      val bookToEnter: Book =
        new BookDBAccess.TestBook(
          "Ruins",
          "Kevin J. Anderson",
          "006105223X",
          Some(
            "Description for Ruins"
          ),
          Some(
            getClass.getResource(
              "/Ruins.jpg"
            ).toURI()
          ),
          Set[Categories](
            "sci-fi",
            "conspiracy"
          )
        )

      When("the information on the book is entered")
      // Display dialog to enter in new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookTab.addButtonId,
        MouseButton.PRIMARY
      )

      // Enter in title of new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.titleControlId,
        MouseButton.PRIMARY
      )
      enterDataIntoControl(
        bookToEnter.title
      )

      // Enter in author of new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.authorControlId,
        MouseButton.PRIMARY
      )
      enterDataIntoControl(
        bookToEnter.author
      )

      // Enter in ISBN of new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.isbnControlId,
        MouseButton.PRIMARY
      )
      enterDataIntoControl(
        bookToEnter.isbn
      )

      // Enter in description of new book
      bookToEnter.description match {
        case Some(description) =>
          bookClientRobot.clickOn(
            NodeQueryUtils hasId BookEntryDialog.descriptionControlId,
            MouseButton.PRIMARY
          )
          enterDataIntoControl(
            description
          )
        case None =>
      }

      // Select cover of new book
      bookToEnter.coverImage match {
        case Some(coverName) =>
          val dialogStage =
            bookClientRobot.listWindows().asScala.find {
              case possibleDialog: javafx.stage.Stage =>
                possibleDialog.getTitle == BookTabControl.addBookTitle
              case _ => false
            }
          dialogStage match {
            case Some(actualStage) =>
              val adaptedStage: scalafx.stage.Window =
                actualStage
                  (coverChooser.selectImage _).expects(
                    adaptedStage
                  ).returning(
                    new File(
                      coverName
                    )
                  )
            case None =>
          }
          bookClientRobot.clickOn(
            NodeQueryUtils hasId BookEntryDialog.bookCoverButtonId,
            MouseButton.PRIMARY
          )
        case None =>
      }

      // Select categories for new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.categorySelectionButtonId,
        MouseButton.PRIMARY
      )
      for (bookCategory <- bookToEnter.categories) {
        selectCategory(
          bookCategory
        )
      }
      bookClientRobot.clickOn(
        NodeQueryUtils hasId CategorySelectionDialog.availableButtonId,
        MouseButton.PRIMARY
      )
      bookClientRobot.clickOn(
        NodeQueryUtils hasId CategorySelectionDialog.saveButtonId,
        MouseButton.PRIMARY
      )

      Then("the information on the book cannot be accepted")
      findBookEntryDialog should haveInactiveSaveButton()
    }

    Scenario("A book that has an ISBN that already exists within the book " +
      "catalog cannot be added to the catalog") {
      Given("the pre-defined categories")
      placePreDefinedCategoriesIntoDatabase()

      And("a populated catalog")
      placeExistingBooksIntoDatabase()
      val catalog: BookCatalog =
        new BookCatalog()

      showMainApplication(
        catalog,
        bookTransactor,
        coverChooser
      )

      And("the information on the book with a duplicate ISBN")
      val bookToEnter: Book =
        new BookDBAccess.TestBook(
          "Ground Zero",
          "Kevin J. Anderson",
          "0061054143",
          Some(
            "Description for Ground Zero"
          ),
          Some(
            getClass.getResource(
              "/GroundZero.jpg"
            ).toURI()
          ),
          Set[Categories](
            "sci-fi",
            "conspiracy"
          )
        )

      When("the information on the book is entered")
      // Display dialog to enter in new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookTab.addButtonId,
        MouseButton.PRIMARY
      )

      // Enter in title of new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.titleControlId,
        MouseButton.PRIMARY
      )
      enterDataIntoControl(
        bookToEnter.title
      )

      // Enter in author of new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.authorControlId,
        MouseButton.PRIMARY
      )
      enterDataIntoControl(
        bookToEnter.author
      )

      // Enter in ISBN of new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.isbnControlId,
        MouseButton.PRIMARY
      )
      enterDataIntoControl(
        bookToEnter.isbn
      )

      // Enter in description of new book
      bookToEnter.description match {
        case Some(description) =>
          bookClientRobot.clickOn(
            NodeQueryUtils hasId BookEntryDialog.descriptionControlId,
            MouseButton.PRIMARY
          )
          enterDataIntoControl(
            description
          )
        case None =>
      }

      // Select cover of new book
      bookToEnter.coverImage match {
        case Some(coverName) =>
          bookClientRobot.listWindows().asScala.find {
            case possibleDialog: javafx.stage.Stage =>
              possibleDialog.getTitle == "Add Book To Catalog"
            case _ => false
          }  match {
            case Some(actualStage) =>
              val adaptedStage: scalafx.stage.Window =
                actualStage
                  (coverChooser.selectImage _).expects(
                    adaptedStage
                  ).returning(
                    new File(
                      coverName
                    )
                  )
            case None =>
          }
          bookClientRobot.clickOn(
            NodeQueryUtils hasId BookEntryDialog.bookCoverButtonId,
            MouseButton.PRIMARY
          )
        case None =>
      }

      // Select categories for new book
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.categorySelectionButtonId,
        MouseButton.PRIMARY
      )
      for (bookCategory <- bookToEnter.categories) {
        selectCategory(
          bookCategory
        )
      }
      bookClientRobot.clickOn(
        NodeQueryUtils hasId CategorySelectionDialog.availableButtonId,
        MouseButton.PRIMARY
      )
      bookClientRobot.clickOn(
        NodeQueryUtils hasId CategorySelectionDialog.saveButtonId,
        MouseButton.PRIMARY
      )

      Then("the information on the book cannot be accepted")
      findBookEntryDialog should haveInactiveSaveButton()
    }
  }
}
