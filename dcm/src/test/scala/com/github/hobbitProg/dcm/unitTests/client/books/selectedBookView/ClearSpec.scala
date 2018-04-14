package com.github.hobbitProg.dcm.unitTests.client.books.selectedBookView

import java.net.URI
import java.util.function.{Consumer, Supplier}

import javafx.application.Application
import javafx.stage.Stage

import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.control.{ListView, TextInputControl}
import scalafx.Includes._

import org.testfx.api.FxToolkit

import org.scalatest.{FreeSpec, Matchers, BeforeAndAfter}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.view.
  SelectedBookView
import com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.view.image.
  CoverImage

/**
  * Specifies how the selected book view is cleared
  * @author Kyle Cranmer
  * @since 0.2
  */
class ClearSpec
    extends FreeSpec
    with BeforeAndAfter
    with Matchers {
  private class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book {
  }

  private var runningApp: Application = _

  after {
    FxToolkit.cleanupStages()
    FxToolkit.cleanupApplication(
      runningApp
    )
  }

  "Given a selected book control" - {
    val selectedBookScene =
      createSelectedBookControlScene

    "and a selected book" - {
      val selectedBook: Book =
        new TestBook(
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
            ClearSpec.titleControlFieldName
          ) should be ("")
       }

        "and there is no author for the selected book" in {
          getTextControlValue(
            selectedBookScene.bookControl,
            ClearSpec.authorControlFieldName
          ) should be ("")
        }

        "and there is no ISBN for the selected book" in {
          getTextControlValue(
            selectedBookScene.bookControl,
            ClearSpec.isbnControlFieldName
          ) should be ("")
        }

        "and there is no description for the selected book" in {
          getTextControlValue(
            selectedBookScene.bookControl,
            ClearSpec.descriptionControlFieldName
          ) should be ("")
        }

        "and there is no cover image for the selected book" in {
          getDisplayedImage(
            selectedBookScene.bookControl
          ) should be (null)
        }

        "and there is no associated categories" in {
          getDisplayedCategories(
            selectedBookScene.bookControl
          ) should be (Set[Categories]())
        }
      }
    }
  }

  // Create scene that contains selected book control
  private def createSelectedBookControlScene : SelectedBookScene = {
    // Create test application
    FxToolkit.registerPrimaryStage()
    runningApp =
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

  // Get value of text control
  def getTextControlValue(
    bookControl: SelectedBookView,
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

  // Get displayed cover image
  def getDisplayedImage(
    bookControl: SelectedBookView
  ): Image = {
    val mirror =
      scala.reflect.runtime.currentMirror
    val titleField =
      mirror.classSymbol(
        bookControl.getClass
      ).toType.members.find {
        member => {
          member.name.toString ==
            ClearSpec.coverImageControlFieldName
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

  // Get displayed categories associated with book
  private def getDisplayedCategories(
    bookControl: SelectedBookView
  ): Set[Categories] = {
    val mirror =
      scala.reflect.runtime.currentMirror
    val titleField =
      mirror.classSymbol(
        bookControl.getClass
      ).toType.members.find {
        member => {
          member.name.toString ==
            ClearSpec.categoryControlFieldName
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

object ClearSpec {
  private val titleControlFieldName: String = "titleValue"
  private val authorControlFieldName: String = "authorValue"
  private val isbnControlFieldName: String = "isbnValue"
  private val descriptionControlFieldName: String = "descriptionValue"
  private val coverImageControlFieldName: String = "coverImageControl"
  private val categoryControlFieldName: String = "categoryControl"
}
