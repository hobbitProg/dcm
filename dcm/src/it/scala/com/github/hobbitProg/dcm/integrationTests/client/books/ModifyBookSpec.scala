package com.github.hobbitProg.dcm.integrationTests.client.books

import org.scalatest.{FeatureSpec, GivenWhenThen, BeforeAndAfter, Matchers}

import org.scalamock.scalatest.MockFactory

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter
import BookCatalogRepositoryInterpreter._
import com.github.hobbitProg.dcm.client.books.dialog.BookEntryDialog
import com.github.hobbitProg.dcm.integrationTests.matchers.scalaTest.
  {IntegrationMatchers, ScalafxMatchers}
import com.github.hobbitProg.dcm.client.dialog.ImageChooser

/**
  * Specification for modifying a book in the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyBookSpec
    extends FeatureSpec
    with GivenWhenThen
    with BeforeAndAfter
    with MockFactory
    with Matchers
    with BookDBAccess
    with GUIAutomation
    with IntegrationMatchers
    with ScalafxMatchers {

  // Chooses cover of book
  private val coverChooser: ImageChooser = mock[ImageChooser]

  before {
    createBookCatalogSchema()
  }

  after {
    // Remove database file
    removeDatabaseFile()

    // Shut down windows
    shutDownApplication
  }

  Feature("The user can modify a book within the book catalog") {
    info("As someone who wants to keep track of books he owns")
    info("I want to change information on books within the book catalog")
    info("So any problems with the books can be fixed")

    Scenario("A book within the book catalog can have its title changed") {
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

      And("the title of the book to modify")
      val titleOfBookToModify: Titles = "Ruins"

      And("the new title of the book")
      val newTitleOfBook: Titles = "Ruinz"

      When("the book to modify is selected")
      selectBookToModify(
        titleOfBookToModify
      )

      And("the title of the book is changed")
      changeTitle(
        titleOfBookToModify,
        newTitleOfBook
      )

      And("the information on the book is accepted")
      acceptBookInformation()

      Then("the updated book is in the catalog")
      val updatedBook: Book =
        new BookDBAccess.TestBook(
          newTitleOfBook,
          "Kevin J. Anderson",
          "0061052477",
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
      getByISBN(
        desktop.bookDisplay.catalog,
        updatedBook.isbn
      ) should beInCatalog(updatedBook)

      And("the updated book is in the repository")
      retrieve(
        updatedBook.isbn
      ) should beInRepository(updatedBook)

      And("the original book is not in the catalog")
      getByTitleAndAuthor(
        desktop.bookDisplay.catalog,
        titleOfBookToModify,
        updatedBook.author
      ) should notBeInCatalog()

      And("the original book is not in the repository")
      retrieve(
        titleOfBookToModify,
        updatedBook.author
      ) should notBeInRepository()

      And("the updated book is displayed on the view displaying the book " +
        "catalog")
      updatedBook should beOn(desktop)

      And("the original book is not displayed on the view displaying the " +
        "book catalog")
      val originalBook: Book =
        new BookDBAccess.TestBook(
          titleOfBookToModify,
          "Kevin J. Anderson",
          "0061052477",
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
      originalBook should notBeOn(desktop)

      And("no books are selected on the window displaying the book catalog")
      desktop should haveNoBooksSelected()

      And("the window displaying the information on the selected book is empty")
      desktop should notHaveSelectedBookDataDisplayed()
    }

    Scenario("A book within the book catalog can have its author changed") {
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

      And("the title of the book to modify")
      val titleOfBookToModify: Titles = "Ruins"

      And("the original author of the book")
      val originalAuthor: Authors =
        "Kevin J. Anderson"

      And("the new author of the book")
      val newAuthor: Authors =
        "Kevin Anderson"

      When("the book to modify is selected")
      selectBookToModify(
        titleOfBookToModify
      )

      And("the author of the book is changed")
      changeAuthor(
        originalAuthor,
        newAuthor
      )

      And("the information on the book is accepted")
      acceptBookInformation()

      Then("the updated book is in the catalog")
      val updatedBook: Book =
        new BookDBAccess.TestBook(
          titleOfBookToModify,
          newAuthor,
          "0061052477",
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
      getByISBN(
        desktop.bookDisplay.catalog,
        updatedBook.isbn
      ) should beInCatalog(updatedBook)

      And("the updated book is in the repository")
      retrieve(
        updatedBook.isbn
      ) should beInRepository(updatedBook)

      And("the original book is not in the catalog")
      getByTitleAndAuthor(
        desktop.bookDisplay.catalog,
        titleOfBookToModify,
        originalAuthor
      ) should notBeInCatalog()

      And("the original book is not in the repository")
      retrieve(
        titleOfBookToModify,
        originalAuthor
      ) should notBeInRepository()

      And("the updated book is displayed on the view displaying the book " +
        "catalog")
      updatedBook should beOn(desktop)

      And("the original book is not displayed on the view displaying the " +
        "book catalog" )
      val originalBook: Book =
        new BookDBAccess.TestBook(
          titleOfBookToModify,
          originalAuthor,
          "0061052477",
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
      originalBook should notBeOn(desktop)

      And("no books are selected on the window displaying the book catalog")
      desktop should haveNoBooksSelected()

      And("the window displaying the information on the selected is empty")
      desktop should notHaveSelectedBookDataDisplayed()
    }

    Scenario("A book within the book catalog can have its ISBN changed") {
      Given("the pre-defined categories")
      And("a populated catalog")
      And("the title of the book to modify")
      And("the original ISBN of the book")
      And("the updated ISBN of the book")
      When("the book of to modifyu is selected")
      And("the ISBN of the book is changed")
      And("the infomation on the book is accepted")
      Then("the updated book is in the catalog")
      And("the updated book is in the repository")
      And("the original book is not in the catalog")
      And("the original book is not in the repository")
      And("the updated book is displayed on the view displaying the book " +
        "catalog")
      And("the original book is not displayed on the view displaying the " +
        "book catalog")
      And("no books are selected on the window displaying the book catalog")
      And("the window displaying the information on the selected book is empty")
    }
  }
}