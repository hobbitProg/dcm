package com.github.hobbitProg.dcm.integrationTests.client.books

import java.io.File
import java.net.URI

import scala.collection.{JavaConverters, Set}
import JavaConverters._


import javafx.scene.input.MouseButton

import scalafx.Includes._
import scalafx.scene.control.{Tab, TabPane}
import scalafx.scene.layout.AnchorPane

import org.scalatest.{FeatureSpec, GivenWhenThen, BeforeAndAfter, Matchers}

import org.scalamock.scalatest.MockFactory

import org.testfx.api.FxRobotContext
import org.testfx.service.query.NodeQuery
import org.testfx.util.NodeQueryUtils

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter
import BookCatalogRepositoryInterpreter._
import com.github.hobbitProg.dcm.client.books.dialog.BookEntryDialog
import com.github.hobbitProg.dcm.client.control.BookTabControl
import com.github.hobbitProg.dcm.client.dialog.{CategorySelectionDialog,
  ImageChooser}
import com.github.hobbitProg.dcm.integrationTests.matchers.JavaConversions._
import com.github.hobbitProg.dcm.integrationTests.matchers.scalaTest.
  {IntegrationMatchers, ScalafxMatchers}

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


  private def findBookTab: Option[javafx.scene.control.Tab] = {
    val context =
      new FxRobotContext

    val catalogPane: TabPane =
      context.getNodeFinder.lookup {
        currentNode : javafx.scene.Node =>
        currentNode.isInstanceOf[javafx.scene.control.TabPane]
      }.query.asInstanceOf[javafx.scene.control.TabPane]
    catalogPane.tabs.find {
      currentTab =>
        currentTab.text.value == "Books"
    }
  }

  feature("The user can modify a book within the book catalog") {
    info("As someone who wants to keep track of books he owns")
    info("I want to change information on books within the book catalog")
    info("So any problems with the books can be fixed")

    scenario("A book within the book catalog can have its title changed") {
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

    scenario("A book within the book catalog can have its author changed") {
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

    scenario("A book within the book catalog can have its ISBN changed") {
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

      And("the original ISBN of the book")
      val originalISBN: ISBNs = "0061052477"

      And("the updated ISBN of the book")
      val updatedISBN: ISBNs = "0061052478"

      When("the book to modify is selected")
      selectBookToModify(
        titleOfBookToModify
      )

      And("the ISBN of the book is changed")
      changeISBN(
        originalISBN,
        updatedISBN
      )

      And("the infomation on the book is accepted")
      acceptBookInformation()

      Then("the updated book is in the catalog")
      val updatedBook: Book =
        new BookDBAccess.TestBook(
          titleOfBookToModify,
          "Kevin J. Anderson",
          updatedISBN,
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
      getByISBN(
        desktop.bookDisplay.catalog,
        originalISBN
      ) should notBeInCatalog()

      And("the original book is not in the repository")
      retrieve(
        originalISBN
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
          originalISBN,
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

    scenario("A book within the book catalog can have its description changed") {
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

      And("the original description of the book")
      val originalDescriptionText: String =
        "Description for Ruins"

      And("the updated description of the book")
      val updatedDescriptionText: String =
        "Description for Ground Zero"

      When("the book to modify is selected")
      selectBookToModify(
        titleOfBookToModify
      )

      And("the description of the book is changed")
      changeDescription(
        originalDescriptionText,
        updatedDescriptionText
      )

      And("the information on the book is accepted")
      acceptBookInformation()

      Then("the updated book is in the catalog")
      val updatedBook: Book =
        new BookDBAccess.TestBook(
          titleOfBookToModify,
          "Kevin J. Anderson",
          "0061052477",
          Some(
            updatedDescriptionText
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

      And("the updated book is displayed on the view displaying the book " +
        "catalog")
      updatedBook should beOn(desktop)

      And("no books are selected on the window displaying the book catalog")
      desktop should haveNoBooksSelected()

      And("the window displaying the information on the selected book is empty")
      desktop should notHaveSelectedBookDataDisplayed()
    }

    scenario("A book within the book catalog can have its cover image changed") {
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

      And("the new cover of the book")
      val newCover: URI =
        getClass.getResource(
          "/Goblins.jpg"
        ).toURI()


      When("the book to modify is selected")
      selectBookToModify(
        titleOfBookToModify
      )

      And("the cover of the book is changed")
      val dialogStage =
        bookClientRobot.listWindows().asScala.find {
          case possibleDialog: javafx.stage.Stage =>
            possibleDialog.getTitle == BookTabControl.modifyBookTitle
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
                  newCover
                )
              )
        case None =>
      }

      selectNewCover()

      And("the information on the book is accepted")
      acceptBookInformation()

      Then("the updated book is in the catalog")
      val updatedBook: Book =
        new BookDBAccess.TestBook(
          titleOfBookToModify,
          "Kevin J. Anderson",
          "0061052477",
          Some(
            "Description for Ruins"
          ),
          Some(
            newCover
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

      And("the updated book is displayed on the view displaying the book " +
        "catalog")
      updatedBook should beOn(desktop)

      And("no books are selected on the window displaying the book catalog")
      desktop should haveNoBooksSelected()

      And("the window displaying the information on the selected book is empty")
      desktop should notHaveSelectedBookDataDisplayed()
    }

    scenario("A book within the book catalog can have its categories modified") {
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

      And("the categories to disassociate from the book")
      val categoriesToDisassociate: Set[Categories] =
        Set[Categories](
          "sci-fi",
          "conspiracy"
        )

      When("the book to modify is selected")
      selectBookToModify(
        titleOfBookToModify
      )

      And("the catetgories are disassociated from the book")
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookEntryDialog.categorySelectionButtonId,
        MouseButton.PRIMARY
      )
      for (bookCategory <- categoriesToDisassociate) {
        selectCategory(
          bookCategory,
          CategorySelectionDialog.selectedCategoriesId
        )
      }
      bookClientRobot.clickOn(
        NodeQueryUtils hasId CategorySelectionDialog.disassociateButtonId,
        MouseButton.PRIMARY
      )
      bookClientRobot.clickOn(
        NodeQueryUtils hasId CategorySelectionDialog.saveButtonId,
        MouseButton.PRIMARY
      )

      And("the information on the book is accepted")
      acceptBookInformation()

      Then("the updated book is in the catalog")
      val updatedBook: Book =
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
          Set[Categories]()
        )
      getByISBN(
        desktop.bookDisplay.catalog,
        updatedBook.isbn
      ) should beInCatalog(updatedBook)

      And("the updated book is in the repository")
      retrieve(
        updatedBook.isbn
      ) should beInRepository(updatedBook)

      And("the updated book is displayed on the view displaying the book" +
        "catalog")
      updatedBook should beOn(desktop)

      And("no books are selected on the window displaying the book catalog")
      desktop should haveNoBooksSelected()

      And("the window displaying the information on the selected book is empty")
      desktop should notHaveSelectedBookDataDisplayed()
    }

    scenario("The modify button is inactive when no books are selected"){
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

      When("no books are selected")
      Then("the modify button is inactive")
      findBookTab should haveDisabledModifyButton()
    }

    scenario("A book within the book catalog cannot be modified when no " +
      "title is specified") {
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

      When("the book to modify is selected")
      selectBookToModify(
        titleOfBookToModify
      )

      And("the title of the book is removed")
      deleteTitle(
        titleOfBookToModify
      )

      Then("the save button on the modify book dialog is inactive")
      findBookEntryDialog("Modify Book") should haveInactiveSaveButton()
    }

    scenario("A book within the book catalog cannot be modified when no " +
      "author is specified") {
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

      When("the book to modify is selected")
      selectBookToModify(
        titleOfBookToModify
      )

      And("the author of the book is removed")
      deleteAuthor(
        originalAuthor
      )

      Then("the save button on the modify book dialog is inactive")
      findBookEntryDialog("Modify Book") should haveInactiveSaveButton()
    }

    scenario("A book within the book catalog cannot be modified when no " +
      "ISBN is specified"){
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

      And("the original ISBN of the book")
      val originalISBN: ISBNs =
        "0061052477"

      When("the book to modify is selected")
      selectBookToModify(
        titleOfBookToModify
      )

      And("the ISBN of the book is removed")
      deleteISBN(
        originalISBN
      )

      Then("the save button on the modify book dialog is inactive")
      findBookEntryDialog("Modify Book") should haveInactiveSaveButton()
    }

    scenario("A book within the book catalog when the modifed title and " +
      "author is associated with a different book") {
      Given("the pre-defined categories")
      And("a populated catalog")
      And("the title of the book to modify")
      And("the title of a different book in the repository")
      And("the author of the different book in the repository")
      When("the book to modify is selected")
      And("the title of the book is changed")
      And("and the author of the book is changed")
      Then("the save button on the modify book dialog is inactive")
      pending
    }

    scenario("A book within the book catalog when the modified ISBN is " +
      "associated with a different book") {
      pending
    }
  }
}
