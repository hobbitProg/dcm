package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import scala.collection.Set

import java.net.URI
import java.util.function.{Consumer, Supplier}

import javafx.application.Application
import javafx.scene.input.{KeyCode, MouseButton}

import org.testfx.api.{FxRobot, FxRobotInterface, FxToolkit}
import org.testfx.util.NodeQueryUtils

import scalafx.Includes._
import scalafx.scene.Scene

import org.scalatest.{BeforeAndAfter, FreeSpec, Matchers}

import org.scalamock.scalatest.MockFactory

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.control.BookDialogParent
import com.github.hobbitProg.dcm.client.books.dialog.{AddBookDialog, BookEntryDialog}
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.dialog.CategorySelectionDialog
import com.github.hobbitProg.dcm.matchers.scalafx.scalatest.ButtonMatchers
import com.github.hobbitProg.dcm.scalafx.ControlRetriever

/**
  * Specification for adding book to the catalog using dialog
  * @author Kyle Cranmer
  * @since 0.2
  */
class AddingBookSpec
    extends FreeSpec
    with BeforeAndAfter
    with MockFactory
    with Matchers
    with ButtonMatchers
    with ControlRetriever {

  class BookData(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) {
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

  // Book data that was added to catalog
  private var newBookData: BookData = _

  // Robot to automate entering in information
  private val newBookRobot: FxRobotInterface =
    new FxRobot

  // Application being run
  private var runningApp: Application = _

  // Valid new book to add
  val bookImageLocation: URI =
    getClass.getResource(
      "/GroundZero.jpg"
    ).toURI

  after {
    FxToolkit.cleanupStages()
    FxToolkit.cleanupApplication(
      runningApp
    )
  }

  "Given the categories that can be associated with books" - {
    val definedCategories: Set[String] =
      Set[String](
        "sci-fi",
        "conspiracy",
        "fantasy",
        "thriller"
      )

    "and valid information on a book to add to the catalog" - {
      val validNewBook: BookData =
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

      "and the catalog that is being updated" - {
        val catalog: BookCatalog =
          new BookCatalog()

        "and the repository to place book catalog information into" - {
          val repository =
            mock[BookCatalogRepository];

          "and the service for the book catalog" - {
            val service =
              new TestService()
            service onAdd {
              data: TestService.BookData =>
              data match {
                case (title, author, isbn, description, coverImage, categories) =>
                  newBookData =
                    new BookData(
                      title,
                      author,
                      isbn,
                      description,
                      coverImage,
                      categories
                    )
              }
            }

            "and the parent window that created the book additon dialog" - {
              val parent =
                new TestParent(
                  catalog
                )

              "when the book dialog is created" - {
                val bookAdditionDialog: Scene =
                  createBookAdditionDialog(
                    catalog,
                    repository,
                    service,
                    definedCategories,
                    parent
                  )

                "and the title of the book is entered" - {
                  activateControl(
                    BookEntryDialog.titleControlId
                  )
                  enterDataIntoControl(
                    validNewBook.title
                  )

                  "and the author of the book is entered" - {
                    activateControl(
                      BookEntryDialog.authorControlId
                    )
                    enterDataIntoControl(
                      validNewBook.author
                    )

                    "and the ISBN of the book is entered" - {
                      activateControl(
                        BookEntryDialog.isbnControlId
                      )
                      enterDataIntoControl(
                        validNewBook.isbn
                      )

                      "and the description of the book is entered" - {
                        activateControl(
                          BookEntryDialog.descriptionControlId
                        )
                        enterDataIntoControl(
                          validNewBook.description match {
                            case Some(existingDescription) => existingDescription
                            case None => ""
                          }
                        )

                        "and the cover for the book is chosen" - {
                          activateControl(
                            BookEntryDialog.bookCoverButtonId
                          )

                          "and the appropriate categories are associated with " +
                          "the book" - {
                            activateControl(
                              BookEntryDialog.categorySelectionButtonId
                            )
                            selectCategory(
                              validNewBook.categories.head
                            )
                            selectCategory(
                              validNewBook.categories.last
                            )
                            activateControl(
                              CategorySelectionDialog.availableButtonId
                            )
                            activateControl(
                              CategorySelectionDialog.saveButtonId
                            )

                            "and the book information is saved" - {
                              activateControl(
                                BookEntryDialog.saveButtonId
                              )

                              "then the book entry dialog is closed" in {
                                (bookAdditionDialog.window.value == null ||
                                  !bookAdditionDialog.window.value.showing.value) should be (true)
                              }

                              "and the book was saved into the catalog" in {
                                newBookData should equal (validNewBook)
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
  }

  "Given the categories that can be associated with books" - {
    val definedCategories: Set[String] =
      Set[String](
        "sci-fi",
        "conspiracy",
        "fantasy",
        "thriller"
      )

    "and information on a book to add to the catalog (without a title)" - {
      val invalidNewBook: BookData =
        new BookData(
          "",
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


      "and the catalog that is being updated" - {
        val catalog: BookCatalog =
          new BookCatalog()

        "and the repository to place book catalog information into" - {
          val repository =
            mock[BookCatalogRepository];

          "and the service for the book catalog" - {
            val service =
              new TestService()

            "and the parent window that created the book additon dialog" - {
              val parent =
                new TestParent(
                  catalog
                )

              "when the book dialog is created" - {
                val bookAdditionDialog: Scene =
                  createBookAdditionDialog(
                    catalog,
                    repository,
                    service,
                    definedCategories,
                    parent
                  )

                "and the author of the book is entered" - {
                  activateControl(
                    BookEntryDialog.authorControlId
                  )
                  enterDataIntoControl(
                    invalidNewBook.author
                  )

                  "and the ISBN of the book is entered" - {
                    activateControl(
                      BookEntryDialog.isbnControlId
                    )
                    enterDataIntoControl(
                      invalidNewBook.isbn
                    )

                    "and the description of the book is entered" - {
                      activateControl(
                        BookEntryDialog.descriptionControlId
                      )
                      enterDataIntoControl(
                        invalidNewBook.description match {
                          case Some(existingDescription) => existingDescription
                          case None => ""
                        }
                      )

                      "and the cover for the book is chosen" - {
                        activateControl(
                          BookEntryDialog.bookCoverButtonId
                        )

                        "and the appropriate categories are associated with " +
                        "the book" - {
                          activateControl(
                            BookEntryDialog.categorySelectionButtonId
                          )
                          selectCategory(
                            invalidNewBook.categories.head
                          )
                          selectCategory(
                            invalidNewBook.categories.last
                          )
                          activateControl(
                            CategorySelectionDialog.availableButtonId
                          )
                          activateControl(
                            CategorySelectionDialog.saveButtonId
                          )

                          "then the book information cannot be saved" in {
                            val saveButton =
                              retrieveSaveButton(
                                bookAdditionDialog
                              )
                            saveButton should be (disabled)
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


  "Given the categories that can be associated with books" - {
    val definedCategories: Set[String] =
      Set[String](
        "sci-fi",
        "conspiracy",
        "fantasy",
        "thriller"
      )

    "and information on a book to add to the catalog (except the author)" - {
      val invalidNewBook: BookData =
        new BookData(
          "Ground Zero",
          "",
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

      "and the catalog that is being updated" - {
        val catalog: BookCatalog =
          new BookCatalog()

        "and the repository to place book catalog information into" - {
          val repository =
            mock[BookCatalogRepository];

          "and the service for the book catalog" - {
            val service =
              new TestService()

            "and the parent window that created the book additon dialog" - {
              val parent =
                new TestParent(
                  catalog
                )

              "when the book dialog is created" - {
                val bookAdditionDialog: Scene =
                  createBookAdditionDialog(
                    catalog,
                    repository,
                    service,
                    definedCategories,
                    parent
                  )

                "and the title of the book is entered" - {
                  activateControl(
                    BookEntryDialog.titleControlId
                  )
                  enterDataIntoControl(
                    invalidNewBook.title
                  )

                  "and the ISBN of the book is entered" - {
                    activateControl(
                      BookEntryDialog.isbnControlId
                    )
                    enterDataIntoControl(
                      invalidNewBook.isbn
                    )

                    "and the description of the book is entered" - {
                      activateControl(
                        BookEntryDialog.descriptionControlId
                      )
                      enterDataIntoControl(
                        invalidNewBook.description match {
                          case Some(existingDescription) => existingDescription
                          case None => ""
                        }
                      )

                      "and the cover for the book is chosen" - {
                        activateControl(
                          BookEntryDialog.bookCoverButtonId
                        )

                        "and the appropriate categories are associated with " +
                        "the book" - {
                          activateControl(
                            BookEntryDialog.categorySelectionButtonId
                          )
                          selectCategory(
                            invalidNewBook.categories.head
                          )
                          selectCategory(
                            invalidNewBook.categories.last
                          )
                          activateControl(
                            CategorySelectionDialog.availableButtonId
                          )
                          activateControl(
                            CategorySelectionDialog.saveButtonId
                          )

                          "then the book information cannot be saved" in {
                            val saveButton =
                              retrieveSaveButton(
                                bookAdditionDialog
                              )
                            saveButton should be (disabled)
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

  "Given the categories that can be associated with books" - {
    val definedCategories: Set[String] =
      Set[String](
        "sci-fi",
        "conspiracy",
        "fantasy",
        "thriller"
      )

    "and information on a book to add to the catalog (except the ISBN)" - {
      val invalidNewBook: BookData =
        new BookData(
          "Ground Zero",
          "Kevin J. Anderson",
          "",
          Some("Description for Ground Zero"),
          Some[URI](
            bookImageLocation
          ),
          Set[String](
            "sci-fi",
            "conspiracy"
          )
        )

      "and the catalog that is being updated" - {
        val catalog: BookCatalog =
          new BookCatalog()

        "and the repository to place book catalog information into" - {
          val repository =
            mock[BookCatalogRepository];

          "and the service for the book catalog" - {
            val service =
              new TestService()
            service onAdd {
              data: TestService.BookData =>
              data match {
                case (title, author, isbn, description, coverImage, categories) =>
                  newBookData =
                    new BookData(
                      title,
                      author,
                      isbn,
                      description,
                      coverImage,
                      categories
                    )
              }
            }

            "and the parent window that created the book additon dialog" - {
              val parent =
                new TestParent(
                  catalog
                )

              "when the book dialog is created" - {
                val bookAdditionDialog: Scene =
                  createBookAdditionDialog(
                    catalog,
                    repository,
                    service,
                    definedCategories,
                    parent
                  )

                "and the title of the book is entered" - {
                  activateControl(
                    BookEntryDialog.titleControlId
                  )
                  enterDataIntoControl(
                    invalidNewBook.title
                  )

                  "and the author of the book is entered" - {
                    activateControl(
                      BookEntryDialog.authorControlId
                    )
                    enterDataIntoControl(
                      invalidNewBook.author
                    )

                    "and the description of the book is entered" - {
                      activateControl(
                        BookEntryDialog.descriptionControlId
                      )
                      enterDataIntoControl(
                        invalidNewBook.description match {
                          case Some(existingDescription) => existingDescription
                          case None => ""
                        }
                      )

                      "and the cover for the book is chosen" - {
                        activateControl(
                          BookEntryDialog.bookCoverButtonId
                        )

                        "and the appropriate categories are associated with " +
                        "the book" - {
                          activateControl(
                            BookEntryDialog.categorySelectionButtonId
                          )
                          selectCategory(
                            invalidNewBook.categories.head
                          )
                          selectCategory(
                            invalidNewBook.categories.last
                          )
                          activateControl(
                            CategorySelectionDialog.availableButtonId
                          )
                          activateControl(
                            CategorySelectionDialog.saveButtonId
                          )

                          "then the book information cannot be saved" in {
                            val saveButton =
                              retrieveSaveButton(
                                bookAdditionDialog
                              )
                            saveButton should be (disabled)
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

  "Given the categories that can be associated with books" - {
    val definedCategories: Set[String] =
      Set[String](
        "sci-fi",
        "conspiracy",
        "fantasy",
        "thriller"
      )

    "and information on a book to add to the catalog (with a title and author " +
    "that already exists in the catalog)" - {
      val invalidNewBook: BookData =
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

      "and the catalog that is being updated" - {
        val catalog: BookCatalog =
          new BookCatalog()

        "and the repository to place book catalog information into" - {
          val repository =
            mock[BookCatalogRepository];

          "and the service for the book catalog" - {
            val service =
              new TestService()
            service.existingTitle =
              invalidNewBook.title
            service.existingAuthor =
              invalidNewBook.author

            "and the parent window that created the book additon dialog" - {
              val parent =
                new TestParent(
                  catalog
                )

              "when the book dialog is created" - {
                val bookAdditionDialog: Scene =
                  createBookAdditionDialog(
                    catalog,
                    repository,
                    service,
                    definedCategories,
                    parent
                  )

                "and the title of the book is entered" - {
                  activateControl(
                    BookEntryDialog.titleControlId
                  )
                  enterDataIntoControl(
                    invalidNewBook.title
                  )

                  "and the author of the book is entered" - {
                    activateControl(
                      BookEntryDialog.authorControlId
                    )
                    enterDataIntoControl(
                      invalidNewBook.author
                    )

                    "and the ISBN of the book is entered" - {
                      activateControl(
                        BookEntryDialog.isbnControlId
                      )
                      enterDataIntoControl(
                        invalidNewBook.isbn
                      )

                      "and the description of the book is entered" - {
                        activateControl(
                          BookEntryDialog.descriptionControlId
                        )
                        enterDataIntoControl(
                          invalidNewBook.description match {
                            case Some(existingDescription) => existingDescription
                            case None => ""
                          }
                        )

                        "and the cover for the book is chosen" - {
                          activateControl(
                            BookEntryDialog.bookCoverButtonId
                          )

                          "and the appropriate categories are associated with " +
                          "the book" - {
                            activateControl(
                              BookEntryDialog.categorySelectionButtonId
                            )
                            selectCategory(
                              invalidNewBook.categories.head
                            )
                            selectCategory(
                              invalidNewBook.categories.last
                            )
                            activateControl(
                              CategorySelectionDialog.availableButtonId
                            )
                            activateControl(
                              CategorySelectionDialog.saveButtonId
                            )

                            "then the book information cannot be saved" in {
                              val saveButton =
                                retrieveSaveButton(
                                  bookAdditionDialog
                                )
                              saveButton should be (disabled)
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

  "Given the categories that can be associated with books" - {
    "and information on a book to add to the catalog (with an ISBN of a book " +
    "that already exists in the catalog)" - {
      "and the catalog that is being updated" - {
        "and the repository to place book catalog information into" - {
          "and the service for the book catalog" - {
            "and the parent window that created the book additon dialog" - {
              "when the book dialog is created" - {
                "and the title of the book is entered" - {
                  "and the author of the book is entered" - {
                    "and the ISBN of the book is entered" - {
                      "and the description of the book is entered" - {
                        "and the cover for the book is chosen" - {
                          "and the appropriate categories are associated with " +
                          "the book" - {
                            "then the book information cannot be saved" in pending
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
    * Create dialog to add book to catalog
    *
    * @param catalog Catalog to add new book to
    * @param repository Repository containing book catalog data
    * @param service Service that handles book catalog
    * @param definedCategories Categories available to be associated with book
    * @param parent Parent window that created book addition dialog
    *
    * @return Dialog to add book to catalog
    */
  private def createBookAdditionDialog(
    catalog: BookCatalog,
    repository: BookCatalogRepository,
    service: BookCatalogService[BookCatalog],
    definedCategories: Set[String],
    parent: BookDialogParent
  ): Scene = {
    // Create mock file chooser
    val coverImageChooser: TestChooser =
      new TestChooser(
        bookImageLocation
      )

    FxToolkit.registerPrimaryStage()
    runningApp =
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
      new AddBookDialog(
        catalog,
        repository,
        service,
        parent,
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

    bookEntryDialog
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
    * Enter data into currently active control
    * @param dataToEnter Data to place into control
    */
  private def enterDataIntoControl(
    dataToEnter: String
  ) = {
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
}
