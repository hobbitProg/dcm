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
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.control.BookDialogParent
import com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.dialog.
  {AddBookDialog, BookEntryDialog}
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.gui.linuxDesktop.dialog.
  {CategorySelectionDialog, ImageChooser}
import com.github.hobbitProg.dcm.matchers.scalafx.scalatest.ButtonMatchers
import com.github.hobbitProg.dcm.scalafx.{ControlRetriever,
  BookDialogHelper}

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
    with ControlRetriever
    with BookDialogHelper {

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
    new AddBookDialog(
      catalog,
      repository,
      service,
      parent,
      coverImageChooser,
      definedCategories
    )

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
                  createBookDialog(
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
                              validNewBook.categories.head,
                              CategorySelectionDialog.availableCategoriesId
                            )
                            selectCategory(
                              validNewBook.categories.last,
                              CategorySelectionDialog.availableCategoriesId
                            )
                            activateControl(
                              CategorySelectionDialog.associateButtonId
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
                  createBookDialog(
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
                            invalidNewBook.categories.head,
                            CategorySelectionDialog.availableCategoriesId
                          )
                          selectCategory(
                            invalidNewBook.categories.last,
                            CategorySelectionDialog.availableCategoriesId
                          )
                          activateControl(
                            CategorySelectionDialog.associateButtonId
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
                  createBookDialog(
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
                            invalidNewBook.categories.head,
                            CategorySelectionDialog.availableCategoriesId
                          )
                          selectCategory(
                            invalidNewBook.categories.last,
                            CategorySelectionDialog.availableCategoriesId
                          )
                          activateControl(
                            CategorySelectionDialog.associateButtonId
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
                  createBookDialog(
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
                            invalidNewBook.categories.head,
                            CategorySelectionDialog.availableCategoriesId
                          )
                          selectCategory(
                            invalidNewBook.categories.last,
                            CategorySelectionDialog.availableCategoriesId
                          )
                          activateControl(
                            CategorySelectionDialog.associateButtonId
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
                  createBookDialog(
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
                              invalidNewBook.categories.head,
                              CategorySelectionDialog.availableCategoriesId
                            )
                            selectCategory(
                              invalidNewBook.categories.last,
                              CategorySelectionDialog.availableCategoriesId
                            )
                            activateControl(
                              CategorySelectionDialog.associateButtonId
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
    val definedCategories: Set[String] =
      Set[String](
        "sci-fi",
        "conspiracy",
        "fantasy",
        "thriller"
      )

    "and information on a book to add to the catalog (with an ISBN of a book " +
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
            service.existingISBN =
              invalidNewBook.isbn

            "and the parent window that created the book additon dialog" - {
              val parent =
                new TestParent(
                  catalog
                )

              "when the book dialog is created" - {
                val bookAdditionDialog: Scene =
                  createBookDialog(
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
                              invalidNewBook.categories.head,
                              CategorySelectionDialog.availableCategoriesId
                            )
                            selectCategory(
                              invalidNewBook.categories.last,
                              CategorySelectionDialog.availableCategoriesId
                            )
                            activateControl(
                              CategorySelectionDialog.associateButtonId
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
}
