package com.github.hobbitProg.dcm.integrationTests.matchers.scalaTest

import scala.collection.Set

import org.scalatest.matchers.{Matcher, MatchResult}

import scalafx.Includes._
import scalafx.scene.control.{ListView, TextInputControl}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{AnchorPane, VBox}

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
    def desktop: DCMDesktop

    // Find given control to check
    def findControl(
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
  }

  /**
    * Base for matching books on book catalog view
    */
  trait BookCatalogViewMatcher
      extends DCMDesktopMatcher {
    /**
      * Verify control containing book catalog meets given condition
      */
    protected def bookCatalogControlVerification(
      assertionPredicate: ListView[Book] => Boolean
    ): Boolean = {
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
          assertionPredicate(
            catalogControl
          )
        case None =>
          false
      }
    }
  }

  class BookDisplayedMatcher(
    val desktop: DCMDesktop
  ) extends BookCatalogViewMatcher
      with Matcher[Book] {
    def apply(
      left: Book
    ) =
      MatchResult(
        bookCatalogControlVerification(
          assertionPredicate(
            left
          )
        ),
        "Book exists within the catalog",
        "Book does not exist within the catalog"
      )

    /**
      * Determine if the given predicate is met by the given book and the book
      * catalog view
      */
    protected def assertionPredicate
      (
        bookToVerify: Book
      )
      (
        bookCatalogControl: ListView[Book]
      ): Boolean  = {
      bookCatalogControl.items.value.toSet contains bookToVerify
    }
  }

  def beOn(
    desktop: DCMDesktop
  ) =
    new BookDisplayedMatcher(
      desktop
    )

  class BookCollectionDisplayedMatcher(
    val desktop: DCMDesktop
  ) extends BookCatalogViewMatcher
      with Matcher[Set[Book]] {
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

    private def allBooksDisplayed(
      relevantBooks: Set[Book]
    ) = {
      relevantBooks forall {
        relevantBook =>
        bookCatalogControlVerification(
          assertionPredicate(
            relevantBook
          )
        )
      }
    }

    /**
      * Determine if the given predicate is met by the given book and the book
      * catalog view
      */
    protected def assertionPredicate
      (
        bookToVerify: Book
      )
      (
        bookCatalogControl: ListView[Book]
      ): Boolean  = {
      bookCatalogControl.items.value.toSet contains bookToVerify
    }
  }

  def allBeOn(
    desktop: DCMDesktop
  ) =
    new BookCollectionDisplayedMatcher(
      desktop
    )

  class NoBookSelectedMatcher
      extends BookCatalogViewMatcher
      with Matcher[DCMDesktop] {

    var desktop: DCMDesktop = _

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

    private def noBookSelected(
      bookCatalogControl : ListView[Book]
    ): Boolean =
      bookCatalogControl.selectionModel.value.isEmpty()
  }

  def haveNoBooksSelected() =
    new NoBookSelectedMatcher()

  class SelectedBookViewClearMatcher
      extends DCMDesktopMatcher
      with Matcher[DCMDesktop] {

    var desktop: DCMDesktop = _

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

    private def noSelectedBookIsDisplayed(): Boolean = {
      val selectedBookControl =
        findControl(
          Class.forName(
            "javafx.scene.Group"
          )
        )
      selectedBookControl match {
        case Some(bookControl) =>
          selectedBookControlTextVerification(
            SelectedBookView.titleControlId,
            bookControl.asInstanceOf[javafx.scene.Group]
          ) &&
          selectedBookControlTextVerification(
            SelectedBookView.authorControlId,
            bookControl.asInstanceOf[javafx.scene.Group]
          ) &&
          selectedBookControlTextVerification(
            SelectedBookView.isbnControlId,
            bookControl.asInstanceOf[javafx.scene.Group]
          ) &&
          selectedBookControlTextVerification(
            SelectedBookView.descriptionControlId,
            bookControl.asInstanceOf[javafx.scene.Group]
          ) &&
          selectedBookControlCoverImageVerification(
            bookControl.asInstanceOf[javafx.scene.Group]
          ) &&
          selectedBookControlCategoriesVerification(
            bookControl.asInstanceOf[javafx.scene.Group]
          )
        case None =>
          false
      }
    }

    /**
      * Verify value of field in selected book text control is empty
      * @param fieldId ID of text control to verify
      * @param bookControl selected book text control
      */
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

    /**
      * Verify cover image of book in selected book control is cleared
      * @param bookControl Selected book control
      */
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

    /**
      * Verify categories of book in selected book control is cleared
      * @param bookControl Selected book control
      */
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

  def notHaveSelectedBookDataDisplayed() =
    new SelectedBookViewClearMatcher()
}

object ScalafxMatchers extends ScalafxMatchers {
}
