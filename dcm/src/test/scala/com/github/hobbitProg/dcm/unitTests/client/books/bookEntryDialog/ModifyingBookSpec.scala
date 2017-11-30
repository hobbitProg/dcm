package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import scala.collection.Set

import java.net.URI
import java.util.function.{Consumer, Supplier}

import javafx.application.Application
import javafx.scene.input.{KeyCode, MouseButton}

import org.testfx.api.{FxRobot, FxRobotInterface, FxToolkit}
import org.testfx.matcher.control.TextInputControlMatchers
import org.testfx.util.NodeQueryUtils

import scalafx.Includes._
import scalafx.scene.Scene

import org.scalatest.{BeforeAndAfter, FreeSpec, Matchers}

import org.scalamock.scalatest.MockFactory

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.control.BookDialogParent
import com.github.hobbitProg.dcm.client.books.dialog.{ModifyBookDialog,
  BookEntryDialog}
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.dialog.{CategorySelectionDialog,
  ImageChooser}
import com.github.hobbitProg.dcm.scalafx.{ControlRetriever,
  BookDialogHelper}

/**
  * Specification for modifying book that exists in catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingBookSpec
    extends FreeSpec
    with BeforeAndAfter
    with MockFactory
    with Matchers
    with ControlRetriever
    with BookDialogHelper {
  class BookData(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book {
    override def equals(
      that: Any
    ): Boolean = {
      that match {
        case that: BookData =>
          title == that.title &&
          author == that.author &&
          isbn == that.isbn &&
          description == that.description &&
          coverImage == that.coverImage &&
          categories == that.categories
        case _ => false
      }
    }

    override def hashCode: Int = {
      var collectedInfo =
        title +
      author +
      isbn
      description match {
        case Some(descriptionText) =>
          collectedInfo =
            collectedInfo + descriptionText
        case None =>
      }
      coverImage match {
        case Some(coverLocation) =>
          collectedInfo =
            collectedInfo + coverLocation.toString
        case None =>
      }
      collectedInfo =
        categories.foldLeft(
          collectedInfo
        ) {
          (gatheredInfo, currentCategory) =>
          gatheredInfo + currentCategory
        }
      collectedInfo.hashCode
    }
  }

  // Robot to automate entering in information
  private val modifyBookRobot: FxRobotInterface =
    new FxRobot

  var originalBook: BookData = _

  after {
    FxToolkit.cleanupStages()
    FxToolkit.cleanupApplication(
      runningApp
    )
  }

  /**
    * Create dialog that is to be tested
    *
    * @param catalog Catalog to add new book to
    * @param repository Repository containing book catalog data
    * @param service Service that handles book catalog
    * @param parent Parent window that created book addition dialog
    * @param coverImageChooser Dialog to choose image for cover
    * @param definedCategories Categories available to be associated with book
    *
    * @return Dialog to be tested
    */
  def createDialog(
    catalog: BookCatalog,
    repository: BookCatalogRepository,
    service: BookCatalogService[BookCatalog],
    parent: BookDialogParent,
    coverImageChooser: ImageChooser,
    definedCategories: Set[String]
  ): BookEntryDialog =
    new ModifyBookDialog(
      catalog,
      repository,
      service,
      parent,
      coverImageChooser,
      definedCategories,
      originalBook
    )

  "Given the categories that can be associated with books" - {
    val definedCategories: Set[String] =
      Set[String](
        "sci-fi",
        "conspiracy",
        "fantasy",
        "thriller"
      )

    "and a book that already exists in the catalog" - {
      originalBook =
        new BookData(
          "Ruins",
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

      "and the new title of the book" - {
        val newTitle: Titles =
          "Ground Zero"

        "and the catalog that is being updated" - {
          val catalog: BookCatalog =
            new BookCatalog()

          "and the repository to place book catalog information into" - {
            val repository =
              mock[BookCatalogRepository];

            "and the service for the book catalog" - {
              val service =
                new TestService()
              var bookToDelete: Book = null
              service.onModify(
                unmodifiedBook =>
                bookToDelete = unmodifiedBook
              )

              "and the parent window that created the book modification " +
              "dialog" - {
                val parent =
                  new TestParent(
                    catalog
                  )

                "when the book dialog is created" - {
                  val bookModificationDialog: Scene =
                    createBookDialog(
                      catalog,
                      repository,
                      service,
                      definedCategories,
                      parent
                    )

                  "and the title of the book is modified" - {
                    activateControl(
                      BookEntryDialog.titleControlId
                    )
                    clearControl(
                      originalBook.title.length()
                    )
                    enterDataIntoControl(
                      newTitle
                    )

                    "and the book information is saved" - {
                      activateControl(
                        BookEntryDialog.saveButtonId
                      )

                      "then the book entry dialog is closed" in {
                        (bookModificationDialog.window.value == null ||
                          !bookModificationDialog.window.value.showing.value) should be (true)
                      }

                      "and the original book is removed via the service" in {
                        bookToDelete should equal (originalBook)
                      }

                      "and the new book is added via the service" in pending
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

}
