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
import com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.dialog.
  BookEntryDialog
import com.github.hobbitProg.dcm.client.control.BookTabControl
import com.github.hobbitProg.dcm.client.gui.linuxDesktop.dialog.
  {CategorySelectionDialog, ImageChooser}
import com.github.hobbitProg.dcm.integrationTests.matchers.JavaConversions._
import com.github.hobbitProg.dcm.integrationTests.matchers.scalaTest.
  {IntegrationMatchers, ScalafxMatchers}
import com.github.hobbitProg.dcm.client.gui.linuxDesktop.BookTab

class DeleteBookSpec
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

  feature("The user can remove a book from the book catalog") {
    info("As someone who wants to keep trak of books he owns")
    info("I want to remove books from the book catalog")
    info ("So that I can know what books I own")

    scenario("A book within the book catalog can be removed") {
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

      And("the title of the book to delete")
      val titleOfBookToDelete: Titles = "Ruins"

      And("the ISBN of the book to delete")
      val isbnOfBookToDelete: ISBNs = "0061052477"

      When("the book to delete is selected")
      selectBook(
        titleOfBookToDelete
      )

      And("the book is deleted")
      bookClientRobot.clickOn(
        NodeQueryUtils hasId BookTab.deleteButtonID,
        MouseButton.PRIMARY
      )

      Then("the book is not in the catalog")
      getByISBN(
        desktop.bookDisplay.catalog,
        isbnOfBookToDelete
      ) should notBeInCatalog()

      And("the book is not in the repository")
      retrieve(
        isbnOfBookToDelete
      ) should notBeInRepository()

      And("no book is selected")
      desktop should haveNoBooksSelected()

      And("the window displaying the information on the selected book is empty")
      desktop should notHaveSelectedBookDataDisplayed()

      And("the delete button is inactive")
      findBookTab should haveDisabledDeleteButton()
    }
  }
}
