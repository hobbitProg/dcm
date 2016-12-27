import java.io.File
import java.sql._
import java.net.URI
import java.util.function.{Consumer, Supplier}
import javafx.scene.Parent
import javafx.scene.input.{KeyCode, MouseButton}
import javafx.stage.{Stage, Window}

import org.jbehave.core.model.ExamplesTable
import org.testfx.api.{FxRobot, FxRobotInterface, FxToolkit}
import org.testfx.util.{NodeQueryUtils, WaitForAsyncUtils}

import scala.collection.Set
import scala.collection.JavaConversions._
import scala.language.implicitConversions
import scalafx.Includes._
import scalafx.stage.FileChooser
import org.scalamock.scalatest.MockFactory

import com.github.hobbitProg.dcm.client.dialog.CategorySelectionDialog
import com.github.hobbitProg.dcm.client.books.Categories
import com.github.hobbitProg.dcm.client.books.bookCatalog.{Book, Catalog}
import com.github.hobbitProg.dcm.client.books.dialog.BookEntryDialog
import com.github.hobbitProg.dcm.client.linuxDesktop.{BookTab, DCMDesktop}

/**
  * Performs steps in stories related to book catalog client
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookCatalogClientSteps
  extends MockFactory {
  // Connection to book catalog
  private var bookConnection: Connection = _

  // Robot to perform steps
  private val bookClientRobot: FxRobotInterface =
    new FxRobot

  // Book to place into catalog
  private var bookToEnter: Book = _

  // Chooses cover of book
  private val coverChooser =
    mock[FileChooser]

  // Convert row from storty to book
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

  @org.jbehave.core.annotations.BeforeStories
  def defineSchemas(): Unit = {
    // Get connection to database
    Class.forName(
      BookCatalogClientSteps.databaseClass
    )
    bookConnection =
      DriverManager.getConnection(
        BookCatalogClientSteps.databaseURL
      )

    // Create schema for categories defined for book
    val schemaStatement: Statement =
      bookConnection.createStatement()
    try {
      schemaStatement execute
        "CREATE TABLE " + BookCatalogClientSteps.definedCategoriesTable + " (" +
          "categoryID integer PRIMARY KEY," +
          BookCatalogClientSteps.categoryColumn + " TINYTEXT" +
          ");"
      schemaStatement execute
        "CREATE TABLE " + BookCatalogClientSteps.bookCatalogTable + " (" +
          "bookID integer PRIMARY KEY," +
          BookCatalogClientSteps.titleColumn + " MEDIUMTEXT," +
          BookCatalogClientSteps.authorColumn + " MEDIUMTEXT," +
          BookCatalogClientSteps.isbnColumn + " MEDIUMTEXT," +
          BookCatalogClientSteps.descriptionColumn + " MEDIUMTEXT," +
          BookCatalogClientSteps.coverColumn + " MEDIUMTEXT" +
          ");"
      schemaStatement execute
        "CREATE TABLE " + BookCatalogClientSteps.categoryMappingTable + " (" +
          "mappingID integer PRIMARY KEY," +
          BookCatalogClientSteps.isbnColumn + " MEDIUMTEXT," +
          BookCatalogClientSteps.categoryColumn + " TINYTEXT" +
          ");"
    }
    catch {
      case sqlProblem: SQLException => System.out.println(sqlProblem.getMessage)
    }
  }

  @org.jbehave.core.annotations.BeforeStories
  def showMainApplication(): Unit = {
    FxToolkit.registerPrimaryStage()
    FxToolkit.setupSceneRoot(
      new Supplier[Parent] {
        override def get(): Parent = {
          new DCMDesktop(
            coverChooser,
            Catalog(
              bookConnection
            ),
            Set[Categories](
              "sci-fi",
              "conspiracy",
              "fantasy",
              "thriller"
            )
          )
        }
      }
    )
    WaitForAsyncUtils.waitForFxEvents()
    FxToolkit.setupStage(
      new Consumer[Stage] {
        override def accept(t: Stage): Unit = {
          if (!t.showingProperty().value) {
            t.show()
          }
        }
      }
    )
    WaitForAsyncUtils.waitForFxEvents()
  }

  @org.jbehave.core.annotations.AfterStories
  def removeTestCatalog(): Unit = {
    val dbFile =
      new File(
        BookCatalogClientSteps.databaseFile
      )
    dbFile.delete()
  }

  @org.jbehave.core.annotations.AfterStories
  @org.jbehave.core.annotations.Pending
  def releaseTestResources(): Unit = {
  }

  @org.jbehave.core.annotations.Given("the following defined categories: $existingCategories")
  def definedCategories(
    existingCategories: ExamplesTable
  ): Unit = {
    val insertStatement =
      bookConnection.createStatement()
    for (definedCategory <- existingCategories.getRows) {
      insertStatement executeUpdate
        "INSERT INTO " +
        BookCatalogClientSteps.definedCategoriesTable +
        " (" +
        BookCatalogClientSteps.categoryColumn +
        ") VALUES ('" +
        definedCategory.get("category") +
        "')"
    }
  }

  @org.jbehave.core.annotations.Given("the following books that are already in the catalog: $preExistingBooks")
  def catalogContents(
    preExistingBooks: ExamplesTable
  ): Unit = {
    val insertStatement =
      bookConnection.createStatement()
    for (existingBook <- preExistingBooks.getRows) {
      insertStatement executeUpdate
        "INSERT INTO " + BookCatalogClientSteps.bookCatalogTable + "(" +
          BookCatalogClientSteps.titleColumn + "," +
          BookCatalogClientSteps.authorColumn + ","  +
          BookCatalogClientSteps.isbnColumn + "," +
          BookCatalogClientSteps.descriptionColumn + "," +
          BookCatalogClientSteps.coverColumn + ")VALUES('" +
          existingBook.get("title") + "','" +
          existingBook.get("author") + "','" +
          existingBook.get("isbn") + "','" +
          existingBook.get("description") + "','" +
          existingBook.get("cover") + "')"

      for (associatedCategory <- existingBook.get("categories").split(",")) {
        insertStatement executeUpdate
          "INSERT INTO " + BookCatalogClientSteps.categoryMappingTable + "(" +
            BookCatalogClientSteps.isbnColumn + "," +
            BookCatalogClientSteps.categoryColumn + ")VALUES('" +
            existingBook.get("isbn") + "','" +
            associatedCategory + "')"
      }
    }
  }

  @org.jbehave.core.annotations.Given("the following book to add to the catalog: $newBook")
    def bookToAdd(
    newBook: ExamplesTable
  ): Unit = {
    bookToEnter = mapAsScalaMap(newBook getRow 0)
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

    // Accept information on new book
    bookClientRobot.clickOn(
      NodeQueryUtils hasId BookEntryDialog.saveButtonId,
      MouseButton.PRIMARY
    )
  }

  @org.jbehave.core.annotations.Then("the book is in the book catalogs")
  @org.jbehave.core.annotations.Pending
  def bookExistsInCatalog(): Unit = {
  }

  @org.jbehave.core.annotations.Then("the book is displayed on the window displaying the book catalog")
  @org.jbehave.core.annotations.Pending
  def newBookIsDisplayedWithinBookCatalog(): Unit = {
  }

  @org.jbehave.core.annotations.Then("the books that were originally on the window displaying the book catalog are still on that window")
  @org.jbehave.core.annotations.Pending
  def originalBooksAreStillDisplayed(): Unit = {
  }

  @org.jbehave.core.annotations.Then("no books are selected on the window displaying the book catalog")
  @org.jbehave.core.annotations.Pending
  def noBooksAreSelectedInBookCatalogWindow(): Unit = {
  }

  @org.jbehave.core.annotations.Then("the window displaying the information on the selected book is empty")
  @org.jbehave.core.annotations.Pending
  def noSelectedBookIsDisplayed(): Unit = {
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
