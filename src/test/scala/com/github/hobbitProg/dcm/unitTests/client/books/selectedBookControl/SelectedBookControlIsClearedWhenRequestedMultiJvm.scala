package com.github.hobbitProg.dcm.unitTests.client.books.selectedBookControl

import java.net.URI
import java.util.function.{Consumer, Supplier}
import javafx.application.Application
import javafx.stage.Stage

import org.testfx.api.FxToolkit
import org.scalatest.{FreeSpec, Matchers}

import scala.collection.Set
import scala.reflect.runtime.universe._

import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.control.{ListView, TextInputControl}
import scalafx.Includes._

import com.github.hobbitProg.dcm.client.books.Categories
import com.github.hobbitProg.dcm.client.books.bookCatalog.Book
import com.github.hobbitProg.dcm.client.books.bookCatalog.Implicits._
import com.github.hobbitProg.dcm.client.books.control.SelectedBookControl
import com.github.hobbitProg.dcm.client.books.control.image.CoverImage

/**
  * Verifies window containing information on currently selected book is
  * cleared when requested
  */
class SelectedBookControlIsClearedWhenRequestedMultiJvm
  extends FreeSpec
    with Matchers {
  "Given a selected book control" - {
    val selectedBookScene =
      createSelectedBookControlScene

    "and a selected book" - {
      val selectedBook: Book =
        (
          "Ruins",
          "Kevin J. Anderson",
          "0061052477",
          Some("Description for Ruins"),
          Some[URI](
            getClass.getResource(
              "/Ruins.jpg"
            ).toURI
          ),
          Set[Categories](
            "sci-fi",
            "conspiracy"
          )
        )
      selectedBookScene.bookControl display selectedBook

      "when the selected book is cleared" - {
        selectedBookScene.bookControl.clear()

        "then there is no title for the selected book" in {
          getTextControlValue(
            selectedBookScene.bookControl,
            SelectedBookControlIsClearedWhenRequestedMultiJvm.
              titleControlFieldName
          ) shouldBe ""
        }

        "and there is no author for the selected book" in {
          getTextControlValue(
            selectedBookScene.bookControl,
            SelectedBookControlIsClearedWhenRequestedMultiJvm.
              authorControlFieldName
          ) shouldBe ""
        }

        "and there is no ISBN for the selected book" in {
          getTextControlValue(
            selectedBookScene.bookControl,
            SelectedBookControlIsClearedWhenRequestedMultiJvm.
              isbnControlFieldName
          ) shouldBe ""
        }

        "and there is no description for the selected book" in {
          getTextControlValue(
            selectedBookScene.bookControl,
            SelectedBookControlIsClearedWhenRequestedMultiJvm.
              descriptionControlFieldName
          ) shouldBe ""
        }

        "and there is no cover image for the selected book" in {
          getDisplayedImage(
            selectedBookScene.bookControl
          ) shouldBe null
        }

        "and the selected book has no associated categories" in {
          getDisplayedCategories(
            selectedBookScene.bookControl
          ) shouldBe Set[Categories]()
        }
      }
    }
  }

  /**
    * Create scene that contains selected book control
    * @return Scene that contains selected book control
    */
  private def createSelectedBookControlScene : SelectedBookScene = {
    // Create test application
    FxToolkit.registerPrimaryStage()
    FxToolkit.setupApplication(
      new Supplier[Application] {
        override def get(): SelectedBookControlUnitTestApplication = {
          new SelectedBookControlUnitTestApplication
        }
      }
    )
    FxToolkit.showStage()

    // Create scene that contains book catalog control
    val bookScene: SelectedBookScene =
      new SelectedBookScene
    FxToolkit.setupStage(
      new Consumer[Stage] {
        override def accept(
          t: Stage
        ): Unit = {
          t.scene =
            bookScene
        }
      }
    )
    FxToolkit.showStage()

    bookScene
  }

  /**
    * Get value of text control
    * @param bookControl Selected book control used in test
    * @param controlFieldName Name of field containing text control to extract
    * @return Value from text control
    */
  def getTextControlValue(
    bookControl: SelectedBookControl,
    controlFieldName: String
  ): String = {
    val mirror =
      scala.reflect.runtime.currentMirror
    val titleField =
      mirror.classSymbol(
        bookControl.getClass
      ).toType.members.find {
        member => {
          member.name.toString == controlFieldName
        }
      }
    val instanceMirror =
      mirror.reflect(
        bookControl
      )
    val textControl: TextInputControl =
      instanceMirror.reflectMethod(
        titleField match {
          case Some(fieldValue) => fieldValue.asMethod
          case None => null
        }
      ).apply().asInstanceOf[TextInputControl]
    textControl.text.value
  }

  /**
    * Get displayed cover image
    * @param bookControl Selected book control used in test
    * @return Displayed cover image
    */
  def getDisplayedImage(
    bookControl: SelectedBookControl
  ): Image = {
    val mirror =
      scala.reflect.runtime.currentMirror
    val titleField =
      mirror.classSymbol(
        bookControl.getClass
      ).toType.members.find {
        member => {
          member.name.toString ==
            SelectedBookControlIsClearedWhenRequestedMultiJvm.
              coverImageControlFieldName
        }
      }
    val instanceMirror =
      mirror.reflect(
        bookControl
      )
    val coverControl: CoverImage =
      instanceMirror.reflectMethod(
        titleField match {
          case Some(fieldValue) => fieldValue.asMethod
          case None => null
        }
      ).apply().asInstanceOf[CoverImage]
    coverControl.image.value
  }

  /**
    * Get displayed categories associated with book
    * @param bookControl Selected book control used in test
    * @return Displayed categories associated with book
    */
  private def getDisplayedCategories(
    bookControl: SelectedBookControl
  ): Set[Categories] = {
    val mirror =
      scala.reflect.runtime.currentMirror
    val titleField =
      mirror.classSymbol(
        bookControl.getClass
      ).toType.members.find {
        member => {
          member.name.toString ==
            SelectedBookControlIsClearedWhenRequestedMultiJvm.
              categoryControlFieldName
        }
      }
    val instanceMirror =
      mirror.reflect(
        bookControl
      )
    val categoryControl: ListView[Categories] =
      instanceMirror.reflectMethod(
        titleField match {
          case Some(fieldValue) => fieldValue.asMethod
          case None => null
        }
      ).apply().asInstanceOf[ListView[Categories]]
    categoryControl.items.value.toSet
  }
}

object SelectedBookControlIsClearedWhenRequestedMultiJvm {
  private val titleControlFieldName: String = "titleValue"
  private val authorControlFieldName: String = "authorValue"
  private val isbnControlFieldName: String = "isbnValue"
  private val descriptionControlFieldName: String = "descriptionValue"
  private val coverImageControlFieldName: String = "coverImageControl"
  private val categoryControlFieldName: String = "categoryControl"
}