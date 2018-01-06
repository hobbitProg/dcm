package com.github.hobbitProg.dcm.integrationTests.client.books

import java.util.function.Supplier

import doobie.util.transactor.Transactor.Aux

import cats.effect.IO

import javafx.application.Application
import javafx.scene.input.{KeyCode, MouseButton}

import org.testfx.api.{FxRobot, FxRobotContext, FxRobotInterface, FxToolkit}
import org.testfx.matcher.control.{ComboBoxMatchers}
import org.testfx.util.{NodeQueryUtils, WaitForAsyncUtils}

import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.interpreter.
  BookCatalogRepositoryInterpreter

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.
  BookCatalogServiceInterpreter
import com.github.hobbitProg.dcm.client.books.dialog.BookEntryDialog
import com.github.hobbitProg.dcm.client.dialog.ImageChooser
import com.github.hobbitProg.dcm.client.linuxDesktop.{DCMDesktop, BookTab}

import com.github.hobbitProg.dcm.integrationTests.matchers.scalafx.
  ListViewCellMatcher

/**
  * Common routines to automate filling in the book information
  *
  * @author Kyle Cranmer
  * @since 0.2
  */
trait GUIAutomation {
  // Distributed catalog manager application
  protected var dcmApplication: Application = _

  // Desktop for distributed catalog manager
  protected var desktop: DCMDesktop = _

  // Robot to perform steps
  protected val bookClientRobot: FxRobotInterface =
    new FxRobot

  // Shut down the test application
  protected def shutDownApplication() = {
    FxToolkit.cleanupStages()
    FxToolkit.cleanupApplication(
      dcmApplication
    )
  }

  // Show the main catalog manager
  protected def showMainApplication(
    catalog: BookCatalog,
    bookTransactor: Aux[IO, Unit],
    coverChooser: ImageChooser
  ): Unit = {
    FxToolkit.registerPrimaryStage()
    BookCatalogRepositoryInterpreter.setConnection(
      bookTransactor
    )
    desktop =
      new DCMDesktop(
        coverChooser,
        catalog,
        BookCatalogServiceInterpreter,
        BookCatalogRepositoryInterpreter
      )

    dcmApplication =
      FxToolkit.setupApplication(
        new Supplier[Application] {
          override def get(): Application = {
            new IntegrationApplication(
              desktop
            )
          }
        }
      )
    FxToolkit.showStage()
  }

  // Select the book to modify
  protected def selectBookToModify(
    title: Titles
  ) = {
    bookClientRobot.clickOn(
      NodeQueryUtils hasText title,
      MouseButton.PRIMARY
    )
    bookClientRobot.clickOn(
      NodeQueryUtils hasId BookTab.modifyButtonID,
      MouseButton.PRIMARY
    )
  }
  // Enter data into currently active control
  protected def enterDataIntoControl(
    dataToEnter: String
  ) = {
    //noinspection ScalaUnusedSymbol,ScalaUnusedSymbol
    dataToEnter.toCharArray foreach {
      case current@upperCase if current.isLetter && current.isUpper =>
        bookClientRobot push(
          KeyCode.SHIFT,
          KeyCode getKeyCode upperCase.toString
        )
      case current@space if current == ' ' =>
        bookClientRobot push KeyCode.SPACE
      case current@period if current == '.' =>
        bookClientRobot push KeyCode.PERIOD
      case current =>
        bookClientRobot push (KeyCode getKeyCode current.toUpper.toString)
    }
  }

  // Activate the control containing the title of the book
  protected def selectTitle() = {
    bookClientRobot.clickOn(
      NodeQueryUtils hasId BookEntryDialog.titleControlId,
      MouseButton.PRIMARY
    )
  }

  // Activate the control containing the author of the book
  protected def selectAuthor() = {
    bookClientRobot.clickOn(
      NodeQueryUtils hasId BookEntryDialog.authorControlId,
      MouseButton.PRIMARY
    )
  }

  // Activate the control containing the ISBN of the book
  protected def selectISBN() = {
    bookClientRobot.clickOn(
      NodeQueryUtils hasId BookEntryDialog.isbnControlId,
      MouseButton.PRIMARY
    )
  }

  // Activate the control containing the description of the book
  protected def selectDescription() = {
    bookClientRobot.clickOn(
      NodeQueryUtils hasId BookEntryDialog.descriptionControlId,
      MouseButton.PRIMARY
    )
  }

  // Select given category
  protected def selectCategory(
    category: Categories,
    parentId: String
  ) = {
    bookClientRobot.press(
      KeyCode.CONTROL
    )
    bookClientRobot.clickOn(
      ListViewCellMatcher.hasText(
        parentId,
        category
      ),
      MouseButton.PRIMARY
    )
    bookClientRobot.release(
      KeyCode.CONTROL
    )
  }

  // Change the title of the book
  protected def changeTitle(
    originalTitle: Titles,
    updatedTitle: Titles
  ) = {
    selectTitle()
    for (titleCharacterIndex <- 1 to originalTitle.length()) {
      bookClientRobot push KeyCode.BACK_SPACE
    }
    enterDataIntoControl(
      updatedTitle
    )
  }

  // Change the author of the book
  protected def changeAuthor(
    originalAuthor: Authors,
    updatedAuthor: Authors
  ) = {
    selectAuthor()
    bookClientRobot push KeyCode.END
    for (authorCharacterIndex <- 1 to originalAuthor.length()) {
      bookClientRobot push KeyCode.BACK_SPACE
    }
    enterDataIntoControl((
      updatedAuthor
    ))
  }

  // Change the ISBN of the book
  protected def changeISBN(
    originalISBN: ISBNs,
    updatedISBN: ISBNs
  ) = {
    selectISBN()
    bookClientRobot push KeyCode.END
    for (isbnCharacterIndex <- 1 to originalISBN.length()) {
      bookClientRobot push KeyCode.BACK_SPACE
    }
    enterDataIntoControl(
      updatedISBN
    )
  }

  // Change the description of the book
  protected def changeDescription(
    originalDescription: String,
    updatedDescription: String
  ) = {
    selectDescription()
    bookClientRobot push KeyCode.END
    for (descriptionIndex <- 1 to originalDescription.length()) {
      bookClientRobot push KeyCode.BACK_SPACE
    }
    enterDataIntoControl(
      updatedDescription
    )
  }

  // Select a new cover image
  protected def selectNewCover() = {
    bookClientRobot.clickOn(
      NodeQueryUtils hasId BookEntryDialog.bookCoverButtonId,
      MouseButton.PRIMARY
    )
  }

  // Accept the information on the book
  def acceptBookInformation() = {
    bookClientRobot.clickOn(
      NodeQueryUtils hasId BookEntryDialog.saveButtonId,
      MouseButton.PRIMARY
    )
  }
}
