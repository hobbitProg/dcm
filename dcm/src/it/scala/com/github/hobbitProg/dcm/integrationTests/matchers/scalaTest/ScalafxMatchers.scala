package com.github.hobbitProg.dcm.integrationTests.matchers.scalaTest

import scala.collection.Set

import org.scalatest.matchers.{Matcher, MatchResult}

import scalafx.Includes._
import scalafx.scene.control.{ListView, TextInputControl}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{AnchorPane, VBox}
import scalafx.stage.Window

import com.github.hobbitProg.dcm.client.linuxDesktop.DCMDesktop
import com.github.hobbitProg.dcm.client.books.view.SelectedBookView
import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * Matchers for verifying data on user interface
  * @author Kyle Cranmer
  * @since 0.2
  */
trait ScalafxMatchers {
  /**
    * Base for verifying view on the DCM desktop
    */
  trait DCMDesktopMatcher {
    /**
      * The window showing the catalog information
      */
    def desktop: DCMDesktop

    /**
      * Find the relevant control to check
      *
      * @param controlType The type of control that is to be verified
      */
    def findControl(
      controlType: Class[_]
    ): Option[javafx.scene.Node] = {
      // Get tab containing the book information
      val existingTabs =
        desktop.tabs.toList
      val possibleBookTab =
        existingTabs.find {
          currentTab =>
          currentTab.getText == "Books"
        }

      // Get the control containing the relevant book catalog information
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
  }

  /**
    * Base for matching books on book catalog view
    */
  trait BookCatalogViewMatcher
      extends DCMDesktopMatcher {
    // Verify the control containing the books within the book catalog meets a
    // given condition
    protected def bookCatalogControlVerification(
      assertionPredicate: ListView[Book] => Boolean
    ): Boolean = {
      // Find the control containing the books within the book catalog
      val possibleBookCatalogControl =
        findControl(
          Class.forName(
            "javafx.scene.control.ListView"
          )
        )

      // Determine if the control containing the books within the book catalog
      // meets the given condition
      possibleBookCatalogControl match {
        case Some(bookCatalogControl) =>
          val catalogControl: ListView[Book] =
            bookCatalogControl.asInstanceOf[javafx.scene.control.ListView[Book]]
          assertionPredicate(
            catalogControl
          )
        case None =>
          false
      }
    }
  }

  /**
    * The matcher that determines if a new book is displayed on the control that
    * displays the entire book catalog
    *
    * @param desktop The window that displays the catalog information
    */
  class BookDisplayedMatcher(
    val desktop: DCMDesktop
  ) extends BookCatalogViewMatcher
      with Matcher[Book] {

    /**
      * Determine if the new book is displayed on the book catalog control
      *
      * @param left The book that was added to the catalog
      *
      * @return The result indicating if the new book is displayed on the book
      * catalog view
      */
    def apply(
      left: Book
    ) =
      MatchResult(
        bookCatalogControlVerification(
          newBookIsBeingDisplayed(
            left
          )
        ),
        "Book exists within the catalog",
        "Book does not exist within the catalog"
      )

    // Determine if the given book is being displayed on the book catalog view
    protected def newBookIsBeingDisplayed (
      bookToVerify: Book
    ) (
      bookCatalogControl: ListView[Book]
    ): Boolean  = {
      bookCatalogControl.items.value.toSet contains bookToVerify
    }
  }

  /**
    * Create a matcher that determines if a new book is displayed on the book
    * catalog view
    *
    * @param desktop The window displaying the catalog information
    *
    * @return A matcher that determines if a new book is displayed on the book
    * catalog view
    */
  def beOn(
    desktop: DCMDesktop
  ) =
    new BookDisplayedMatcher(
      desktop
    )

  /**
    * The matcher that determines if the books originally in the book catalog
    * are still displayed on the book catalog view
    *
    * @param desktop The window that displays the catalog information
    */
  class BookCollectionDisplayedMatcher(
    val desktop: DCMDesktop
  ) extends BookCatalogViewMatcher
      with Matcher[Set[Book]] {

    /**
      *  Determine if the books originally in the book catalog are still
      *  displayed on the book catalog view
      *
      * @param left The books that were originally in the book catalog
      *
      * @return The result indicating if the books that were originally in the
      * catalog are still displayed on the book catalog view
      */
    def apply(
      left: Set[Book]
    ) =
      MatchResult(
        allBooksDisplayed(
          left
        ),
        "Books exists within the catalog",
        "Books do not exist within the catalog"
      )

    // Determine if all of the books that were originally in the catalog are
    // still displayed on the book catalog vier
    private def allBooksDisplayed(
      relevantBooks: Set[Book]
    ) = {
      relevantBooks forall {
        relevantBook =>
        bookCatalogControlVerification(
          originalBookIsStillDisplayed(
            relevantBook
          )
        )
      }
    }

    // Determine if the book that was originally in the book catalog is still
    // being displayed on the book catalog view
    protected def originalBookIsStillDisplayed(
      bookToVerify: Book
    ) (
      bookCatalogControl: ListView[Book]
    ): Boolean  =
      bookCatalogControl.items.value.toSet contains bookToVerify
  }

  /**
    * Create a matcher that determines if all of the books that were originally
    * in the book catalog are still displayed on the book catalog view
    *
    * @param desktop The window displaying the catalog information
    *
    * @return A matcher that determines if all of the books that were originally
    * in the book catalog are still displayed on the book catalog view
    */
  def allBeOn(
    desktop: DCMDesktop
  ) =
    new BookCollectionDisplayedMatcher(
      desktop
    )

  /**
    * The matcher that determines if no books within the book catalog view are
    * currently selected
    */
  class NoBookSelectedMatcher
      extends BookCatalogViewMatcher
      with Matcher[DCMDesktop] {

    /**
      * The window that displays the catalog information
      */
    var desktop: DCMDesktop = _

    /**
      * Determine if no books within the book catalog view are currently
      * selected
      *
      * @param left The window that displays the catalog information
      *
      * @return The result indicating if no books are selected within the book
      * catalog view
      */
    def apply(
      left: DCMDesktop
    ) = {
      desktop = left
      MatchResult(
        bookCatalogControlVerification(
          noBookSelected
        ),
        "No books are currently selected",
        "Books are currently selected"
      )
    }

    // The predicate indicating if no books are selected in the book catalog
    // view
    private def noBookSelected(
      bookCatalogControl : ListView[Book]
    ): Boolean =
      bookCatalogControl.selectionModel.value.isEmpty()
  }

  /**
    * Create a matcher that determines if no books are selected in the book
    * catalog view
    *
    * @return A matcher that determines if no books are selected in the book
    * catalog view
    */
  def haveNoBooksSelected() =
    new NoBookSelectedMatcher()

  /**
    * The matcher that determines no information on a selected book is displayed
    */
  class SelectedBookViewClearMatcher
      extends DCMDesktopMatcher
      with Matcher[DCMDesktop] {

    // The window that displays the catalog information
    var desktop: DCMDesktop = _

    /**
      * Determine if no information on a selected book is displayed
      *
      * @param left The window that displays the catalog information
      *
      * @return The result indicating if no information on a selected book is
      * displayed
      */
    def apply(
      left: DCMDesktop
    ) = {
      desktop = left
      MatchResult(
        noSelectedBookIsDisplayed(),
        "No book data is displayed",
        "Book data is displayed"
      )
    }

    // Determine if no information on a selected book is displayed
    private def noSelectedBookIsDisplayed(): Boolean = {
      // Get the selected book view
      val selectedBookControl =
        findControl(
          Class.forName(
            "javafx.scene.Group"
          )
        )

      selectedBookControl match {
        case Some(bookControl) =>
          // Ensure no title is displayed
          selectedBookControlTextVerification(
            SelectedBookView.titleControlId,
            bookControl.asInstanceOf[javafx.scene.Group]
          ) &&
          // Ensure no author is displayed
          selectedBookControlTextVerification(
            SelectedBookView.authorControlId,
            bookControl.asInstanceOf[javafx.scene.Group]
          ) &&
          // Ensure no ISBN is displayed
          selectedBookControlTextVerification(
            SelectedBookView.isbnControlId,
            bookControl.asInstanceOf[javafx.scene.Group]
          ) &&
          // Ensure no description is displayed
          selectedBookControlTextVerification(
            SelectedBookView.descriptionControlId,
            bookControl.asInstanceOf[javafx.scene.Group]
          ) &&
          // Ensure no cover image is displayed
          selectedBookControlCoverImageVerification(
            bookControl.asInstanceOf[javafx.scene.Group]
          ) &&
          // Ensure no categories are displayed
          selectedBookControlCategoriesVerification(
            bookControl.asInstanceOf[javafx.scene.Group]
          )
        case None =>
          // Could not find the selected book view
          false
      }
    }

    // Verify value of field in selected book text control is empty
    private def selectedBookControlTextVerification(
      fieldId: String,
      bookControl: scalafx.scene.Group
    ): Boolean = {
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
          fieldNodeControl.text.value == ""
        case None =>
          false
      }
    }

    // Verify cover image of book in selected book control is cleared
    private def selectedBookControlCoverImageVerification(
      bookControl: scalafx.scene.Group
    ): Boolean = {
      val controlChildrenPane: AnchorPane =
        bookControl.children.head.asInstanceOf[javafx.scene.layout.AnchorPane]
      val nodeToVerify =
        controlChildrenPane.children.find {
          childNode =>
          childNode.id.value == SelectedBookView.coverImageControlId
        }
      nodeToVerify match {
        case Some(coverNode) =>
          val fieldNodeLayout: VBox =
            coverNode.asInstanceOf[javafx.scene.layout.VBox]
          val fieldNodeControl: ImageView =
            fieldNodeLayout.children.head.asInstanceOf[javafx.scene.image.ImageView]
          fieldNodeControl.image.value == null
        case None =>
          false
      }
    }

    // Verify categories of book in selected book control is cleared
    private def selectedBookControlCategoriesVerification(
      bookControl: scalafx.scene.Group
    ): Boolean = {
      val controlChildrenPane: AnchorPane =
        bookControl.children.head.asInstanceOf[javafx.scene.layout.AnchorPane]
      val nodeToVerify =
        controlChildrenPane.children.find {
          childNode =>
          childNode.id.value == SelectedBookView.categoriesControlId
        }
      nodeToVerify match {
        case Some(categoriesNode) =>
          val categoriesControl: ListView[Categories] =
            categoriesNode.asInstanceOf[javafx.scene.control.ListView[Categories]]
          categoriesControl.items.value.size() == 0
        case None =>
          false
      }
    }
  }

  /**
    * Create a matcher that determines if no selected book information is being
    * displayed
    *
    * @return A matcher that determines if no selected book information is being
    * displayed
    */
  def notHaveSelectedBookDataDisplayed() =
    new SelectedBookViewClearMatcher()

  /**
    * The matcher that determines if the button to save the book information is
    * not active
    */
  class DeactivatedSaveButtonMatcher
      extends Matcher[Window] {
    /**
      * Determine if the button to save the book information is not active
      *
      * @param left The book entry dialog
      *
      * @return The result indicating if the button to save the book information
      * is not active
      */
    def apply(
      left: Window
    ) =
      MatchResult(
        saveButtonInactive(
          left
        ),
        "Save button is inactive",
        "Save button is active"
      )

    // Determine if the button to save the book information is not active
    private def saveButtonInactive(
      bookEntryDialog: Window
    ): Boolean = {
      if (bookEntryDialog == null) {
        false
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
            saveButtonNode.disable.value
          case None =>
            false
        }
      }
    }
  }

  /**
    * Create a matcher that determines if the save button is inactive
    *
    * @return A matcher that determines if the save button is inactive
    */
  def haveInactiveSaveButton() =
    new DeactivatedSaveButtonMatcher()
}

object ScalafxMatchers extends ScalafxMatchers {
}
