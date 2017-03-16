import doobie.imports._

import java.io.File
import java.net.URI
import java.sql._
import java.util.function.{Consumer, Predicate, Supplier}

import javafx.application.Application
import javafx.scene.Parent
import javafx.scene.input.{KeyCode, MouseButton}
import javafx.stage.Stage

import org.jbehave.core.model.ExamplesTable

import org.junit.Assert

import org.testfx.api.{FxRobot, FxRobotContext, FxRobotInterface, FxToolkit}
import org.testfx.util.{NodeQueryUtils, WaitForAsyncUtils}

import scala.collection.Set
import scala.collection.convert.ImplicitConversions._
import scala.language.implicitConversions

import scalafx.Includes._
import scalafx.scene.control.{ListView, TextInputControl}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{AnchorPane, VBox}
import scalafx.stage.{FileChooser, Window}

//import scalaz._
//import Scalaz._
//import scalaz.concurrent.Task

import org.scalamock.scalatest.MockFactory

import com.github.hobbitProg.dcm.acceptanceTests.AcceptanceApplication
import com.github.hobbitProg.dcm.client.dialog.CategorySelectionDialog
import com.github.hobbitProg.dcm.client.books._
import com.github.hobbitProg.dcm.client.books.bookCatalog.{Book, Catalog}
//import com.github.hobbitProg.dcm.client.books.bookCatalog.storage.Storage
import com.github.hobbitProg.dcm.client.books.control.SelectedBookControl
import com.github.hobbitProg.dcm.client.books.dialog.BookEntryDialog
import com.github.hobbitProg.dcm.client.linuxDesktop.{BookTab, DCMDesktop}

/**
  * Performs steps in stories related to book catalog client
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookCatalogClientSteps
  extends MockFactory {
  type BookRow = (Titles,Authors,ISBNs,String,String)

  // Performs transactions on book catalog
//  private val bookTransactor =
//    DriverManagerTransactor[Task](
//      BookCatalogClientSteps.databaseClass,
//      BookCatalogClientSteps.databaseURL
//    )

  // Robot to perform steps
  private val bookClientRobot: FxRobotInterface =
    new FxRobot

  // Book to place into catalog
  private var bookToEnter: Book = _

  // Chooses cover of book
  private val coverChooser: FileChooser = mock[FileChooser]

  // Desktop for distributed catalog manager
  private var desktop: DCMDesktop = _

  // Distributed catalog manager application
  private var dcmApplication: Application = _

  // Books already in books
  private var existingBooks: Set[Book] =
    Set[Book]()

  // Convert row from story to book
  private implicit def row2Book(
    bookRow: scala.collection.mutable.Map[String, String]
  ): Book = {
    new Book(
      bookRow.get("title") match {
        case Some(title) => title
        case None => ""
      },
      bookRow.get("author") match {
        case Some(author) => author
        case None => ""
      },
      bookRow.get("isbn") match {
        case Some(isbn) => isbn
        case None => ""
      },
      bookRow.get("description") match {
        case Some(description) =>
          if (description == "") None else Some(description)
        case None => None
      },
      bookRow.get("cover image") match {
        case Some(cover) =>
          if (cover == "") {
            None
          }
          else {
            val coverLocation =
              getClass.getResource(
                cover
              ).toURI
            Some(coverLocation)
          }
        case None => None
      },
      bookRow.get("categories") match {
        case Some(categories) =>
          Set(categories.split(","): _*)
        case None => Set()
      }
    )
  }

  private implicit def sqlRow2Book(
    bookRow: BookRow
  ) : Book = {
    var descriptionValue: Descriptions =
      bookRow._4 match {
        case "NULL" => None
        case actualDescription => Some(actualDescription)
      }
    val coverLocation =
      bookRow._5 match {
        case "NULL" => None
        case actualCover => Some(new URI(actualCover))
      }
//    val associatedCategories: Set[Categories] =
//      sql"SELECT Category FROM categoryMapping WHERE ISBN=${bookRow._3};"
//      .query[Categories]
//      .vector
//      .transact(bookTransactor)
//      .unsafePerformSync
//      .toSet
    new Book(
//      bookRow._1,
//      bookRow._2,
//      bookRow._3,
      "",
      "",
      "",
      descriptionValue,
      coverLocation,
      //      associatedCategories
      Set()
    )
  }

  // Create predicate based on given anonymous routine
  private implicit def anonymousPredicateToJavaPredicate[PredicateDomain](
    anonymousPredicate: PredicateDomain => Boolean
  ): java.util.function.Predicate[PredicateDomain] = {
    new Predicate[PredicateDomain] {
      override def test(t: PredicateDomain): Boolean = {
        anonymousPredicate(
          t
        )
      }
    }
  }

  @org.jbehave.core.annotations.BeforeScenario
  def defineSchemas(): Unit = {
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
//    schemaCreation.transact(
//      bookTransactor
//    ).unsafePerformSync
  }

  @org.jbehave.core.annotations.AfterScenario
  def removeTestCatalog(): Unit = {
    val dbFile =
      new File(
        BookCatalogClientSteps.databaseFile
      )
    dbFile.delete()
  }

  @org.jbehave.core.annotations.AfterScenario
  def releaseTestResources(): Unit = {
    FxToolkit.cleanupStages()
    FxToolkit.cleanupApplication(
      dcmApplication
    )
  }

  @org.jbehave.core.annotations.Given("the following defined categories: $existingCategories")
  def definedCategories(
    existingCategories: ExamplesTable
  ): Unit = {
    for (definedCategoryRow <- existingCategories.getRows) {
      val definedCategory =
        definedCategoryRow.get("category")
//.      sql"INSERT INTO definedCategories (Category) VALUES ($definedCategory);"
//        .update
//        .run
//        .transact(
//          bookTransactor
//        ).unsafePerformSync
    }
  }

  @org.jbehave.core.annotations.Given("the following books that are already in the catalog: $preExistingBooks")
  def catalogContents(
    preExistingBooks: ExamplesTable
  ): Unit = {
    for (existingBook <- preExistingBooks.getRows) {
      val title =
        existingBook.get("title")
      val author =
        existingBook.get("author")
      val isbn =
        existingBook.get("isbn")
      val description =
        existingBook.get("description")
      val coverImage =
        existingBook.get("cover image")
//      sql"INSERT INTO bookCatalog(Title,Author,ISBN,Description,Cover)VALUES($title,$author,$isbn,$description,$coverImage);"
//        .update
//        .run
//        .transact(
//          bookTransactor
//        ).unsafePerformSync

      for (associatedCategory <- existingBook.get("categories").split(",")) {
//        sql"INSERT INTO categoryMapping(ISBN,Category)VALUES($isbn,$associatedCategory)"
//          .update
//          .run
//          .transact(
//            bookTransactor
//          ).unsafePerformSync
      }

      existingBooks =
        existingBooks +
        new Book(
          title,
          author,
          isbn,
          Some(
            description
          ),
          Some(
            new URI(
              coverImage
            )
          ),
          Set[Categories](
            existingBook.get("categories").split(","): _*
          )
        )
    }

    showMainApplication()
  }

  @org.jbehave.core.annotations.Given("the following book to add to the catalog: $newBook")
  def bookToAdd(
    newBook: ExamplesTable
  ): Unit = {
    val bookRow : scala.collection.mutable.Map[String, String] =
      newBook getRow 0
    bookToEnter = bookRow
  }

  @org.jbehave.core.annotations.When("I enter this book into the book catalog")
  def addBooksToCatalog(): Unit = {
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
          bookClientRobot.listWindows().find {
            case possibleDialog: javafx.stage.Stage =>
              possibleDialog.getTitle == BookTab.addBookTitle
            case _ => false
          }
        dialogStage match {
          case Some(actualStage) =>
            val adaptedStage: scalafx.stage.Window =
              actualStage
            (coverChooser.showOpenDialog _).expects(
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
  }
  @org.jbehave.core.annotations.When("I accept the information on the book")
  def acceptEnteredBook(): Unit = {
    bookClientRobot.clickOn(
      NodeQueryUtils hasId BookEntryDialog.saveButtonId,
      MouseButton.PRIMARY
    )
  }


  @org.jbehave.core.annotations.Then("the book is in the book catalogs")
  def bookExistsInCatalog(): Unit = {
//    val newBooksInCatalog: Set[BookRow] =
//      sql"SELECT Title,Author,ISBN,Description,Cover FROM bookCatalog WHERE ISBN = ${bookToEnter.isbn};"
//      .query[BookRow]
//      .vector
//      .transact(
//        bookTransactor
//      )
//      .unsafePerformSync
//      .toSet
//    val newBook: Book =
//      newBooksInCatalog.head
//    Assert.assertEquals(
//      "New book not placed into catalog",
//      bookToEnter,
//      newBook
//    )
  }

  @org.jbehave.core.annotations.Then("the book is displayed on the window displaying the book catalog")
  def newBookIsDisplayedWithinBookCatalog(): Unit = {
    bookCatalogControlVerification(
      "New book is not displayed",
      catalogControl =>
        catalogControl.items.value.toSet contains bookToEnter
    )
  }

  @org.jbehave.core.annotations.Then("the books that were originally on the window displaying the book catalog are still on that window")
  def originalBooksAreStillDisplayed(): Unit = {
    bookCatalogControlVerification(
      "Existing books are not displayed in control",
      catalogControl =>
        existingBooks forall {
          book =>
            catalogControl.items.value.toSet[Book] contains book
        }
    )
  }

  @org.jbehave.core.annotations.Then("no books are selected on the window displaying the book catalog")
  def noBooksAreSelectedInBookCatalogWindow(): Unit = {
    bookCatalogControlVerification(
      "Book is still selected",
      catalogControl =>
        catalogControl.selectionModel.value.isEmpty
    )
  }

  @org.jbehave.core.annotations.Then("the window displaying the information on the selected book is empty")
  def noSelectedBookIsDisplayed(): Unit = {
    val selectedBookControl =
      findControl(
        Class.forName(
          "javafx.scene.Group"
        )
      )
    selectedBookControl match {
      case Some(bookControl) =>
        selectedBookControlTextVerification(
          "Title not cleared",
          SelectedBookControl.titleControlId,
          bookControl.asInstanceOf[javafx.scene.Group]
        )
        selectedBookControlTextVerification(
          "Author not cleared",
          SelectedBookControl.authorControlId,
          bookControl.asInstanceOf[javafx.scene.Group]
        )
        selectedBookControlTextVerification(
          "ISBN not cleared",
          SelectedBookControl.isbnControlId,
          bookControl.asInstanceOf[javafx.scene.Group]
        )
        selectedBookControlTextVerification(
          "Description not cleared",
          SelectedBookControl.descriptionControlId,
          bookControl.asInstanceOf[javafx.scene.Group]
        )
        selectedBookControlCoverImageVerification(
          bookControl.asInstanceOf[javafx.scene.Group]
        )
      case None =>
        Assert fail "Could not retrieve selected book control"
    }
  }

  @org.jbehave.core.annotations.Then("I cannot accept the information on the book")
  def enteredBookCannotBeAccepted(): Unit = {
    val context =
      new FxRobotContext
    val bookEntryDialogPredicate: java.util.function.Predicate[javafx.stage.Window] =
      (currentWindow: javafx.stage.Window) => {
        val convertedWindow: scalafx.stage.Stage = currentWindow.asInstanceOf[javafx.stage.Stage]
        convertedWindow.title.value == "Add Book To Catalog"
      }
    val bookEntryDialog: Window =
      context.getWindowFinder.window(
        bookEntryDialogPredicate
      )
    if (bookEntryDialog == null) {
      Assert fail "Could not find book entry dialog"
    }
    else {
      val bookEntryPane: AnchorPane =
        bookEntryDialog.scene.value.content.head.asInstanceOf[javafx.scene.layout.AnchorPane]
      val saveButton =
        bookEntryPane.children.find {
          childNode =>
            childNode match {
              case buttonNode: javafx.scene.control.Button => buttonNode.text.value == "Save"
              case _ => false
            }
        }
      saveButton match {
        case Some(saveButtonNode) =>
          Assert.assertTrue(
            "Save button is enabled",
            saveButtonNode.disable.value
          )
        case None =>
          Assert fail "Could not find save button"
      }
    }
  }

  private def showMainApplication(): Unit = {
    FxToolkit.registerPrimaryStage()
//    desktop =
//      new DCMDesktop(
//        coverChooser,
//        Storage(
//          bookTransactor
//        )
//      )

    dcmApplication =
      FxToolkit.setupApplication(
        new Supplier[Application] {
          override def get(): Application = {
            new AcceptanceApplication(
              desktop
            )
          }
        }
      )
    FxToolkit.showStage()
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

  /**
    * Find given control to check
    * @param controlType Type of control to check
    * @return Control to check
    */
  private def findControl(
    controlType: Class[_]
  ): Option[javafx.scene.Node] = {
    // Get tab containing book information
    val existingTabs =
      desktop.tabs.toList
    val possibleBookTab =
      existingTabs.find {
        currentTab =>
          currentTab.getText == "Books"
      }

    // Get control containing book catalog information
    possibleBookTab match {
      case Some(bookTab) =>
        val adaptedTab: scalafx.scene.control.Tab =
          bookTab
        val bookTabPane: AnchorPane =
          adaptedTab.content.value.asInstanceOf[javafx.scene.layout.AnchorPane]
        bookTabPane.children.find {
          control =>
            control.getClass.equals(
              controlType
            )
        }
      case None => None
      }

  }

  /**
    * Verify control containing book catalog meets given condition
    * @param message Message indicating given condition failed
    * @param assertionPredicate Predicate to check given property of control containing book catalog
    */
  private def bookCatalogControlVerification(
    message: String,
    assertionPredicate: ListView[Book] => Boolean
  ) = {
    // Verify new book is displayed in control
    val possibleBookCatalogControl =
      findControl(
        Class.forName(
          "javafx.scene.control.ListView"
        )
      )
    possibleBookCatalogControl match {
      case Some(bookCatalogControl) =>
        val catalogControl: ListView[Book] =
          bookCatalogControl.asInstanceOf[javafx.scene.control.ListView[Book]]
        Assert.assertTrue(
          message,
          assertionPredicate(
            catalogControl
          )
        )
      case None =>
        Assert fail "Could not retrieve book catalog control"
    }
  }

  /**
    * Verify value of field in selected book text control is empty
    * @param message Message indicating given text field isn't empty
    * @param fieldId ID of text control to verify
    * @param bookControl selected book text control
    */
  private def selectedBookControlTextVerification(
    message: String,
    fieldId: String,
    bookControl: scalafx.scene.Group
  ) = {
    val controlChildrenPane: AnchorPane =
      bookControl.children.head.asInstanceOf[javafx.scene.layout.AnchorPane]
    val nodeToVerify =
      controlChildrenPane.children.find(
        childNode =>
          childNode.id.value == fieldId
      )
    nodeToVerify match {
      case Some(fieldNode) =>
        val fieldNodeControl: TextInputControl =
          fieldNode.asInstanceOf[javafx.scene.control.TextInputControl]
        Assert.assertEquals(
          message,
          "",
          fieldNodeControl.text.value
        )
      case None =>
        Assert.fail(
          "Could not find field " + fieldId
        )
    }
  }

  /**
    * Verify cover image of book in selected book control is cleared
    * @param bookControl Selected book control
    */
  private def selectedBookControlCoverImageVerification(
    bookControl: scalafx.scene.Group
  ) = {
    val controlChildrenPane: AnchorPane =
      bookControl.children.head.asInstanceOf[javafx.scene.layout.AnchorPane]
    val nodeToVerify =
      controlChildrenPane.children.find {
        childNode =>
          childNode.id.value == SelectedBookControl.coverImageControlId
      }
    nodeToVerify match {
      case Some(coverNode) =>
        val fieldNodeLayout: VBox =
          coverNode.asInstanceOf[javafx.scene.layout.VBox]
        val fieldNodeControl: ImageView =
          fieldNodeLayout.children.head.asInstanceOf[javafx.scene.image.ImageView]
        Assert.assertNull(
          "Cover image for selected book has not been cleared",
          fieldNodeControl.image.value
        )
      case None =>
        Assert fail
          "Could not find field " + SelectedBookControl.coverImageControlId
    }
  }

  /**
    * Verify categories of book in selected book control is cleared
    * @param bookControl Selected book control
    */
  private def selectedBookControlCategoriesVerification(
    bookControl: scalafx.scene.Group
  ) = {
    val controlChildrenPane: AnchorPane =
      bookControl.children.head.asInstanceOf[javafx.scene.layout.AnchorPane]
    val nodeToVerify =
      controlChildrenPane.children.find {
        childNode =>
          childNode.id.value == SelectedBookControl.categoriesControlId
      }
    nodeToVerify match {
      case Some(categoriesNode) =>
        val categoriesControl: ListView[Categories] =
          categoriesNode.asInstanceOf[javafx.scene.control.ListView[Categories]]
        Assert.assertEquals(
          "Categories for selected book has not been cleared",
          0,
          categoriesControl.items.value.size()
        )
      case None =>
        Assert fail
          "Could not find field " + SelectedBookControl.categoriesControlId
    }
  }
}

object BookCatalogClientSteps {
  private val databaseFile: String =
    "bookCatalogClient.db"
  private val databaseClass: String =
    "org.sqlite.JDBC"
  private val databaseURL: String =
    "jdbc:sqlite:" + databaseFile

  private val definedCategoriesTable: String =
    "definedCategories"
  private val bookCatalogTable: String =
    "bookCatalog"
  private val categoryMappingTable: String =
    "categoryMapping"

  private val categoryColumn: String =
    "Category"
  private val titleColumn: String =
    "Title"
  private val authorColumn: String =
    "Author"
  private val isbnColumn: String =
    "ISBN"
  private val descriptionColumn: String =
    "Description"
  private val coverColumn: String =
    "Cover"
}
