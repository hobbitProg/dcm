package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import java.io.File
import java.net.URI
import java.util.function.{Consumer, Supplier}

import javafx.application.Application
import javafx.scene.input.{KeyCode, MouseButton}

import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.stage.FileChooser

import org.scalamock.scalatest.MockFactory

import org.testfx.api.{FxRobot, FxRobotInterface, FxToolkit}
import org.testfx.util.NodeQueryUtils

import org.scalamock.scalatest.MockFactory

import org.scalatest.{FreeSpec, Matchers}

import scala.collection.Set

import com.github.hobbitProg.dcm.client.dialog.CategorySelectionDialog
import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.BookCatalog
import com.github.hobbitProg.dcm.client.books.dialog.BookEntryDialog

import scalafx.scene.layout.AnchorPane

/**
  * Verifies dialog that allows books to be edited can add books to catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookEntryDialogAllowsUsersToAddBooksToCatalog
  extends FreeSpec
    with MockFactory
    with Matchers {
  class BookData(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) {
  }

  // Robot to automate entering in information
  val newBookRobot: FxRobotInterface =
    new FxRobot

  // Valid new book to add
  val bookImageLocation: URI =
    getClass.getResource(
      "/GroundZero.jpg"
    ).toURI
  private val validNewBook: BookData =
    new BookData(
      "Ground Zero",
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
  private val bookWithDuplicateTitleAndAuthor: BookData =
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
      ))

  "Given a book catalog" - {
    val catalog =
      new TestCatalog()

    "and a repository for the book catalog" - {
      val repository =
        mock[BookRepository]

      "and a collection of defined categories" - {
        val definedCategories: Set[String] =
          Set[String](
            "sci-fi",
            "conspiracy",
            "fantasy",
            "thriller"
          )

        "and dialog to fill with details of book to add to catalog" - {
          val bookAdditionDialog: Scene =
            createBookAdditionDialog(
              catalog,
              repository,
              definedCategories
            )

          "when the user enters the title of the new book" - {
            activateControl(
              BookEntryDialog.titleControlId
            )
            enterDataIntoControl(
              validNewBook.title
            )

            "and the user enters the author of the new book" - {
              activateControl(
                BookEntryDialog.authorControlId
              )
              enterDataIntoControl(
                validNewBook.author
              )

              "and the user enters the ISBN of the new book" - {
                activateControl(
                  BookEntryDialog.isbnControlId
                )
                enterDataIntoControl(
                  validNewBook.isbn
                )

                "and the user enters the description of the new book" - {
                  activateControl(
                    BookEntryDialog.descriptionControlId
                  )
                  enterDataIntoControl(
                    validNewBook.description match {
                      case Some(existingDescription) => existingDescription
                      case None => ""
                    }
                  )

                  "and the user selects the cover image for the new book" - {
                    activateControl(
                      BookEntryDialog.bookCoverButtonId
                    )

                    "and the user requests to associate categories with the new" +
                    " book" - {
                      activateControl(
                        BookEntryDialog.categorySelectionButtonId
                      )

                      "and the user selects the first category with the new " +
                      "book" - {
                        selectCategory(
                          validNewBook.categories.head
                        )

                        "and the user selects the second category with the new " +
                        "book" - {
                          selectCategory(
                            validNewBook.categories.last
                          )
                          activateControl(
                            CategorySelectionDialog.availableButtonId
                          )
                          activateControl(
                            CategorySelectionDialog.saveButtonId
                          )

                          "and the user accepts the information on the new " +
                          "book" - {
                            activateControl(
                              BookEntryDialog.saveButtonId
                            )

                            "then the dialog is closed" in {
                              (bookAdditionDialog.window.value == null ||
                                !bookAdditionDialog.window.value.showing.value) should be (true)
                            }

                            "and the book was added to the catalog" in {
                              catalog.newTitle should be (validNewBook.title)
                              catalog.newAuthor should be (validNewBook.author)
                              catalog.newISBN should be (validNewBook.isbn)
                              catalog.newDescription should be (validNewBook.description)
                              catalog.newCover should be (validNewBook.coverImage)
                              catalog.newCategories should be (validNewBook.categories)
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
      }
    }
  }

  "Given a book catalog" - {
    val catalog =
      new TestCatalog()

    "and a repository for the book catgalog" - {
      val repository =
        mock[BookRepository]

      "and a collection of defined categories" - {
        val definedCategories: Set[String] =
          Set[String](
            "sci-fi",
            "conspiracy",
            "fantasy",
            "thriller"
          )

        "and dialog to fill with details of book to add to catalog" - {
          val bookAdditionDialog: Scene =
            createBookAdditionDialog(
              catalog,
              repository,
              definedCategories
            )

          "when the user enters the author of the new book" - {
            activateControl(
              BookEntryDialog.authorControlId
            )
            enterDataIntoControl(
              validNewBook.author
            )

            "and the user enters the ISBN of the new book" - {
              activateControl(
                BookEntryDialog.isbnControlId
              )
              enterDataIntoControl(
                validNewBook.isbn
              )

              "and the user enters the description of the new book" - {
                activateControl(
                  BookEntryDialog.descriptionControlId
                )
                enterDataIntoControl(
                  validNewBook.description match {
                    case Some(existingDescription) => existingDescription
                    case None => ""
                  }
                )

                "and the user selects the cover image for the new book" - {
                  activateControl(
                    BookEntryDialog.bookCoverButtonId
                  )

                  "and the user requests to associate categories with the new book" - {
                    activateControl(
                      BookEntryDialog.categorySelectionButtonId
                    )

                    "and the user selects the first category with the new book" - {
                      selectCategory(
                        validNewBook.categories.head
                      )

                      "and the user selects the second category with the new book" - {
                        selectCategory(
                          validNewBook.categories.last
                        )
                        activateControl(
                          CategorySelectionDialog.availableButtonId
                        )
                        activateControl(
                          CategorySelectionDialog.saveButtonId
                        )

                        "then the user cannot accept the information on the new book" in {
                          val dialogPane: AnchorPane =
                            bookAdditionDialog.content.head.asInstanceOf[javafx.scene.layout.AnchorPane]
                          val saveButton =
                            dialogPane.children.find {
                              childControl =>
                              childControl match {
                                case childButton: javafx.scene.control.Button =>
                                  childButton.getText == "Save"
                                case _ => false
                              }
                            }

                          saveButton match {
                            case Some(saveControl) =>
                              saveControl.disable.value shouldBe true
                            case None =>
                              fail(
                                "Save button not found"
                              )
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
    }
  }

  "Given a book catalog" - {
    val catalog =
      new TestCatalog()

    "and a repository for the book catalog" - {
      val repository =
        mock[BookRepository]

      "and a collection of defined categories" - {
        val definedCategories: Set[String] =
          Set[String](
            "sci-fi",
            "conspiracy",
            "fantasy",
            "thriller"
          )

        "and dialog to fill with details of book to add to catalog" - {
          val bookAdditionDialog: Scene =
            createBookAdditionDialog(
              catalog,
              repository,
              definedCategories
            )

          "when the user enters the title of the new book" - {
            activateControl(
              BookEntryDialog.titleControlId
            )
            enterDataIntoControl(
              validNewBook.title
            )

            "and the user enters the ISBN of the new book" - {
              activateControl(
                BookEntryDialog.isbnControlId
              )
              enterDataIntoControl(
                validNewBook.isbn
              )

              "and the user enters the description of the new book" - {
                activateControl(
                  BookEntryDialog.descriptionControlId
                )
                enterDataIntoControl(
                  validNewBook.description match {
                    case Some(existingDescription) => existingDescription
                    case None => ""
                  }
                )

                "and the user selects the cover image for the new book" - {
                  activateControl(
                    BookEntryDialog.bookCoverButtonId
                  )

                  "and the user requests to associate categories with the new book" - {
                    activateControl(
                      BookEntryDialog.categorySelectionButtonId
                    )

                    "and the user selects the first category with the new book" - {
                      selectCategory(
                        validNewBook.categories.head
                      )

                      "and the user selects the second category with the new book" - {
                        selectCategory(
                          validNewBook.categories.last
                        )
                        activateControl(
                          CategorySelectionDialog.availableButtonId
                        )
                        activateControl(
                          CategorySelectionDialog.saveButtonId
                        )

                        "then the user cannot accept the information on the new book" in {
                          val dialogPane: AnchorPane =
                            bookAdditionDialog.content.head.asInstanceOf[javafx.scene.layout.AnchorPane]
                          val saveButton =
                            dialogPane.children.find {
                              childControl =>
                              childControl match {
                                case childButton: javafx.scene.control.Button =>
                                  childButton.getText == "Save"
                                case _ => false
                              }
                            }

                          saveButton match {
                            case Some(saveControl) =>
                              saveControl.disable.value shouldBe true
                            case None =>
                              fail(
                                "Save button not found"
                              )
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
    }
  }

  "Given a book catalog" - {
    val catalog =
      new TestCatalog()

    "and a repository for the book catalog" - {
      val repository =
        mock[BookRepository]

      "and a collection of defined categories" - {
        val definedCategories: Set[String] =
          Set[String](
            "sci-fi",
            "conspiracy",
            "fantasy",
            "thriller"
          )

        "and a dialog to fill with the details of the book to add to the " +
        "catalog" - {
          val bookAdditionDialog: Scene =
            createBookAdditionDialog(
              catalog,
              repository,
              definedCategories
            )

          "when the user enters the title of the new book" - {
            activateControl(
              BookEntryDialog.titleControlId
            )
            enterDataIntoControl(
              validNewBook.title
            )

            "and the user enters the author of the new book" - {
              activateControl(
                BookEntryDialog.authorControlId
              )
              enterDataIntoControl(
                validNewBook.author
              )

              "and the user enters the description of the new book" - {
                activateControl(
                  BookEntryDialog.descriptionControlId
                )
                enterDataIntoControl(
                  validNewBook.description match {
                    case Some(existingDescription) => existingDescription
                    case None => ""
                  }
                )

                "and the user selects the cover image for the new book" - {
                  activateControl(
                    BookEntryDialog.bookCoverButtonId
                  )

                  "and the user requests to associate categories with the " +
                  "new book" - {
                    activateControl(
                      BookEntryDialog.categorySelectionButtonId
                    )

                    "and the user selects the first category with the new " +
                    "book" - {
                      selectCategory(
                        validNewBook.categories.head
                      )

                      "and the user selects the second category with the " +
                      "new book" - {
                        selectCategory(
                          validNewBook.categories.last
                        )
                        activateControl(
                          CategorySelectionDialog.availableButtonId
                        )
                        activateControl(
                          CategorySelectionDialog.saveButtonId
                        )

                        "then the user cannot accept the information on " +
                        "the new book" in {
                          val dialogPane: AnchorPane =
                            bookAdditionDialog.content.head.asInstanceOf[javafx.scene.layout.AnchorPane]
                          val saveButton =
                            dialogPane.children.find {
                              childControl =>
                              childControl match {
                                case childButton: javafx.scene.control.Button =>
                                  childButton.getText == "Save"
                                case _ => false
                              }
                            }

                          saveButton match {
                            case Some(saveControl) =>
                              saveControl.disable.value shouldBe true
                            case None =>
                              fail(
                                "Save button not found"
                              )
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
    }
  }

  "Given a book catalog that contains books" - {
    val catalog =
      new TestCatalog()

    "and a repository for the book catalog" - {
      val repository =
        mock[BookRepository]

      "and a collection of defined categories" - {
        val definedCategories: Set[String] =
          Set[String](
            "sci-fi",
            "conspiracy",
            "fantasy",
            "thriller"
          )

        "and a dialog to fill with the details of the book to add to the " +
        "catalog" - {
          val bookAdditionDialog: Scene =
            createBookAdditionDialog(
              catalog,
              repository,
              definedCategories
            )

          "when the user enters the title of the new book (which already " +
          "exists within the catalog)" - {
            activateControl(
              BookEntryDialog.titleControlId
            )
            enterDataIntoControl(
              bookWithDuplicateTitleAndAuthor.title
            )

            "and the user enters the author of the new book (which also " +
            "already exists within the catalog)" - {
              activateControl(
                BookEntryDialog.authorControlId
              )
              enterDataIntoControl(
                bookWithDuplicateTitleAndAuthor.author
              )

              "and the user enters the ISBN of the new book" - {
                activateControl(
                  BookEntryDialog.isbnControlId
                )
                enterDataIntoControl(
                  bookWithDuplicateTitleAndAuthor.isbn
                )

                "and the user enters the description of the new book" - {
                  activateControl(
                    BookEntryDialog.descriptionControlId
                  )
                  enterDataIntoControl(
                    bookWithDuplicateTitleAndAuthor.description match {
                      case Some(existingDescription) => existingDescription
                      case None => ""
                    }
                  )

                  "and the user selects the cover image for the new book" - {
                    activateControl(
                      BookEntryDialog.bookCoverButtonId
                    )

                    "and the user requests to associate categories with the " +
                    "new book" - {
                      activateControl(
                        BookEntryDialog.categorySelectionButtonId
                      )

                      "and the user selects the first category with the new " +
                      "book" - {
                        selectCategory(
                          bookWithDuplicateTitleAndAuthor.categories.head
                        )

                        "and the user selects the second category with the " +
                        "new book" - {
                          selectCategory(
                            bookWithDuplicateTitleAndAuthor.categories.last
                          )
                          activateControl(
                            CategorySelectionDialog.availableButtonId
                          )
                          activateControl(
                            CategorySelectionDialog.saveButtonId
                          )

                          "then the user cannot accept the information on " +
                          "the new book" in {
                            val dialogPane: AnchorPane =
                              bookAdditionDialog.content.head.asInstanceOf[javafx.scene.layout.AnchorPane]
                            val saveButton =
                              dialogPane.children.find {
                                childControl =>
                                childControl match {
                                  case childButton: javafx.scene.control.Button =>
                                    childButton.getText == "Save"
                                  case _ => false
                                }
                              }

                            saveButton match {
                              case Some(saveControl) =>
                                saveControl.disable.value shouldBe true
                              case None =>
                                fail(
                                  "Save button not found"
                                )
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
      }
    }
  }

  /**
    * Activate control to edit
    * @param controlId ID of control to activate
    */
  private def activateControl(
    controlId: String
  ) = {
    newBookRobot.clickOn(
      NodeQueryUtils hasId controlId,
      MouseButton.PRIMARY
    )
  }

  /**
    * Select given category
    * @param category Category to select
    */
  private def selectCategory(
    category: String
  ) = {
    newBookRobot.press(
      KeyCode.CONTROL
    )
    newBookRobot.clickOn(
      NodeQueryUtils hasText category,
      MouseButton.PRIMARY
    )
    newBookRobot.release(
      KeyCode.CONTROL
    )
  }

  /**
    * Enter data into currently active control
    * @param dataToEnter Data to place into control
    */
  private def enterDataIntoControl(
    dataToEnter: String
  ) = {
    //noinspection ScalaUnusedSymbol,ScalaUnusedSymbol
    dataToEnter.toCharArray foreach {
      case current@upperCase if current.isLetter && current.isUpper =>
        newBookRobot push(
          KeyCode.SHIFT,
          KeyCode getKeyCode upperCase.toString
        )
      case current@space if current == ' ' =>
        newBookRobot push KeyCode.SPACE
      case current@period if current == '.' =>
        newBookRobot push KeyCode.PERIOD
      case current =>
        newBookRobot push (KeyCode getKeyCode current.toUpper.toString)
    }
  }

  /**
    * Create dialog to add book to catalog
    *
    * @param definedCategories Categories available to be associated with book
    *
    * @return Dialog to add book to catalog
    */
  private def createBookAdditionDialog(
    catalog: BookCatalog,
    repository: BookRepository,
    definedCategories: Set[String]
  ): Scene = {
  // Create mock file chooser
    val coverImageChooser =
      mock[FileChooser]

    // Create test application
    FxToolkit.registerPrimaryStage()
    FxToolkit.setupApplication(
      new Supplier[Application] {
        override def get(): BookEntryUnitTestApplication = {
          new BookEntryUnitTestApplication
        }
      }
    )
    FxToolkit.showStage()

    // Create dialog to add book to catalog
    val bookEntryDialog =
      new BookEntryDialog(
        catalog,
        repository,
        coverImageChooser,
        definedCategories
      )
    val bookEntryStage: javafx.stage.Stage =
      FxToolkit.setupStage(
        new Consumer[javafx.stage.Stage] {
          override def accept(t: javafx.stage.Stage): Unit = {
            t.scene = bookEntryDialog
          }
        }
      )

    // Create expectations for mock file chooser
    (coverImageChooser.showOpenDialog _).expects(
      jfxStage2sfx(
        bookEntryStage
      )
    ).returning(
      new File(
        bookImageLocation
      )
    )

    bookEntryDialog
  }
}
