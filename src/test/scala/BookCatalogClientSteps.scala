import java.io.File
import java.sql._

import org.jbehave.core.model.ExamplesTable

import scala.collection.JavaConversions._

/**
  * Performs steps in stories related to book catalog client
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookCatalogClientSteps {
  // Connection to book catalog
  private var bookConnection: Connection = _

  @org.jbehave.core.annotations.BeforeStory
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

  @org.jbehave.core.annotations.AfterStory
  def removeTestCatalog(): Unit = {
    val dbFile =
      new File(
        BookCatalogClientSteps.databaseFile
      )
    dbFile.delete()
  }

  @org.jbehave.core.annotations.AfterStory
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
  @org.jbehave.core.annotations.Pending
  def bookToAdd(
    newBook: ExamplesTable
  ): Unit = {
  }

  @org.jbehave.core.annotations.When("I enter this books into the book catalog")
  @org.jbehave.core.annotations.Pending
  def addBooksToCatalog(): Unit = {
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
