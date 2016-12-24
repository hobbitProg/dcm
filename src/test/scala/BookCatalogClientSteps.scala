import java.sql._

import org.jbehave.core.annotations._
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

  @BeforeStory
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
          BookCatalogClientSteps.categoryColumn + " CHAR(25)" +
          ");"
    }
    catch {
      case sqlProblem: SQLException => System.out.println(sqlProblem.getMessage)
    }
  }

  @AfterStory
  @Pending
  def removeTestCatalog(): Unit = {
  }

  @AfterStory
  @Pending
  def releaseTestResources(): Unit = {
  }

  @Given("the following defined categories: <existingCategories>")
  def definedCategories(
    @Named("existingCategories")
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
        ") VALUES (" +
        definedCategory.get("category") +
        ")"
    }
  }

  @Given("the following books that are already in the catalog: <preExistingBooks>")
  @Pending
  def catalogContents(
    @Named("preExistingBooks")
    preExistingBooks: ExamplesTable
  ): Unit = {
  }

  @Given("the following book to add to the catalog: <newBook>")
  @Pending
  def bookToAdd(
    @Named("newBook")
    newBook: ExamplesTable
  ): Unit = {
  }

  @When("I enter this books into the book catalog")
  @Pending
  def addBooksToCatalog(): Unit = {
  }

  @Then("the book is in the book catalogs")
  @Pending
  def bookExistsInCatalog(): Unit = {
  }

  @Then("the book is displayed on the window displaying the book catalog")
  @Pending
  def newBookIsDisplayedWithinBookCatalog(): Unit = {
  }

  @Then("the books that were originally on the window displaying the book catalog are still on that window")
  @Pending
  def originalBooksAreStillDisplayed(): Unit = {
  }

  @Then("no books are selected on the window displaying the book catalog")
  @Pending
  def noBooksAreSelectedInBookCatalogWindow(): Unit = {
  }

  @Then("the window displaying the information on the selected book is empty")
  @Pending
  def noSelectedBookIsDisplayed(): Unit = {
  }
}

object BookCatalogClientSteps {
  private val databaseClass: String =
    "org.sqlite.JDBC"
  private val databaseURL: String =
    "jdbc:sqlite:bookCatalogClient.db"

  private val definedCategoriesTable: String =
    "definedCategories"
  private val categoryColumn: String =
    "category"
}
