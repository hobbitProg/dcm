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
    with MockFactory {
  private class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book {
  }
  private val existingBooks: Set[Book] =
    Set[Book](
      new TestBook(
        "Ruins",
        "Kevin J. Anderson",
        "0061052477",
        Some(
          "Description for Ruins"
        ),
        Some(
          getClass.getResource(
            "/Ruins.jpg"
          ).toURI
        ),
        Set[Categories](
          "sci-fi",
          "conspiracy"
        )
      ),
      new TestBook(
        "Goblins",
        "Charles Grant",
        "0061054143",
        Some(
          "Description for Goblins"
        ),
        Some(
          getClass.getResource(
            "/Goblins.jpg"
          ).toURI()
        ),
        Set[Categories](
          "sci-fi",
          "conspiracy"
        )
      )
    )

  // Chooses cover of book
  private val coverChooser: ImageChooser = mock[ImageChooser]

  // Performs transactions on book catalog
  private val bookTransactor =
   Transactor.fromDriverManager[IO](
      AddBookSpec.databaseClass,
      AddBookSpec.databaseURL
    )

  // Robot to perform steps
  private val bookClientRobot: FxRobotInterface =
    new FxRobot

  // Distributed catalog manager application
  private var dcmApplication: Application = _

  // Desktop for distributed catalog manager
  private var desktop: DCMDesktop = _

  before {
    // Create schema for categories defined for book
    val definedCategoriesSchemaCreationStatement =
      sql"""
        CREATE TABLE definedCategories (
          categoryID integer PRIMARY KEY,
          Category TINYTEXT
        )
      """
    val bookCatalogSchemaCreationStatement =
      sql"""
        CREATE TABLE bookCatalog (
          bookID integer PRIMARY KEY,
          Title MEDIUMTEXT NOT NULL,
          Author MEDIUMTEXT NOT NULL,
          ISBN MEDIUMTEXT NOT NULL,
          Description MEDIUMTEXT,
          Cover MEDIUMTEXT
        );
      """
    val categoryMappingSchemaCreationStatement =
      sql"""
        CREATE TABLE categoryMapping (
          mappingID integer PRIMARY KEY,
          ISBN MEDIUMTEXT,
          Category TINYTEXT
        );
      """
    val schemaCreation =
      for {
        definedCategoriesCreation <- definedCategoriesSchemaCreationStatement.update.run
        bookCatalogCreation <- bookCatalogSchemaCreationStatement.update.run
        categoryMappingCreation <- categoryMappingSchemaCreationStatement.update.run
      } yield definedCategoriesCreation + bookCatalogCreation + categoryMappingCreation
    schemaCreation.transact(
      bookTransactor
    ).unsafeRunSync
  }

  after {
    // Remove database file
    val dbFile =
      new File(
        AddBookSpec.databaseFile
      )
    dbFile.delete()

    // Shut down windows
    FxToolkit.cleanupStages()
    FxToolkit.cleanupApplication(
      dcmApplication
    )
  }

  // Show the main catalog manager
  private def showMainApplication(
    catalog: BookCatalog
  ): Unit = {
    FxToolkit.registerPrimaryStage()
    BookCatalogRepositoryInterpreter.setConnection(
      bookTransactor
    )
    desktop =
      new DCMDesktop(
        coverChooser,
        catalog,
        BookCatalogServiceInterpreter,
        BookCatalogRepositoryInterpreter
      )

    dcmApplication =
      FxToolkit.setupApplication(
        new Supplier[Application] {
          override def get(): Application = {
            new IntegrationApplication(
              desktop
            )
          }
        }
      )
    FxToolkit.showStage()
  }

  // Add the pre-defined categories to the database
  private def placePreDefinedCategoriesIntoDatabase() = {
    for (definedCategory <- AddBookSpec.definedCategories) {
      sql"INSERT INTO definedCategories (Category) VALUES ($definedCategory);"
        .update
        .run
        .transact(
          bookTransactor
        ).unsafeRunSync
    }
  }

  // Place the existing books into the database
  private def placeExistingBooksIntoDatabase() = {
    for (existingBook <- existingBooks) {
      val description: String =
        existingBook.description match {
          case Some(definedDescription) =>
            definedDescription
          case None =>
            ""
        }
      val coverImage: String =
        existingBook.coverImage match {
          case Some(definedCover) =>
            definedCover.toString()
          case None =>
            ""
          }
      sql"INSERT INTO bookCatalog(Title,Author,ISBN,Description,Cover)VALUES(${existingBook.title},${existingBook.author},${existingBook.isbn},$description,$coverImage);"
        .update
        .run
        .transact(
          bookTransactor
        ).unsafeRunSync

      for(associatedCategory <- existingBook.categories) {
        sql"INSERT INTO categoryMapping(ISBN,Category)VALUES(${existingBook.isbn},$associatedCategory)"
          .update
          .run
          .transact(
            bookTransactor
          ).unsafeRunSync
      }
    }
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
        bookClientRobot push(
          KeyCode.SHIFT,
          KeyCode getKeyCode upperCase.toString
        )
      case current@space if current == ' ' =>
        bookClientRobot push KeyCode.SPACE
      case current@period if current == '.' =>
        bookClientRobot push KeyCode.PERIOD
      case current =>
        bookClientRobot push (KeyCode getKeyCode current.toUpper.toString)
    }
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
        catalog
      )

      And("a book to add to the catalog")
      val bookToEnter: Book =
        new TestBook(
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
                possibleDialog.getTitle == BookTab.addBookTitle
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
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.saveButtonId,
        MouseButton.PRIMARY
      )

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
      existingBooks should allBeOn(desktop)

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
        catalog
      )

      And("the information on the book without a title")
      val bookToEnter: Book =
        new TestBook(
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
                possibleDialog.getTitle == BookTab.addBookTitle
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
        catalog
      )

      And("the information on the book without an author")
      val bookToEnter: Book =
        new TestBook(
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
                possibleDialog.getTitle == BookTab.addBookTitle
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
        catalog
      )

      And("the information on the book without an ISBN")
      val bookToEnter: Book =
        new TestBook(
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
                possibleDialog.getTitle == BookTab.addBookTitle
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
        catalog
      )

      And("the information on the book with a duplicate title/author pair")
      val bookToEnter: Book =
        new TestBook(
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
                possibleDialog.getTitle == BookTab.addBookTitle
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
        catalog
      )

      And("the information on the book with a duplicate ISBN")
      val bookToEnter: Book =
        new TestBook(
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
          val dialogStage =
            bookClientRobot.listWindows().asScala.find {
              case possibleDialog: javafx.stage.Stage =>
                possibleDialog.getTitle == BookTab.addBookTitle
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
  }
}

object AddBookSpec {
  private val definedCategories: Set[Categories] =
    Set(
      "sci-fi",
      "conspiracy",
      "fantasy",
      "thriller"
    )

  private val databaseFile: String =
    "bookCatalogClient.db"
  private val databaseClass: String =
    "org.sqlite.JDBC"
  private val databaseURL: String =
    "jdbc:sqlite:" + databaseFile
}
