package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model

import org.scalacheck.{Gen, Arbitrary, Prop}
import Arbitrary.arbitrary
import Gen.const
import Prop.{forAllNoShrink, BooleanOperators, ExtendedBoolean}

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

import scala.collection.{Seq, Set}
import scala.util.{Try, Failure, Success}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.model.interpreter.BookCatalogInterpreter

class AddingBookSpec
    extends Specification
    with ScalaCheck{
  private type BookDataType = (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories])
  private val availableCovers =
    Seq(
      "/Goblins.jpg",
      "/GroundZero.jpg",
      "/Ruins.jpg"
    ).map(
      image =>
      Some(
        getClass().
          getResource(
            image
          ).toURI
      )
    )
  implicit val catalogGenerator = for {
    catalog <- new BookCatalogInterpreter
  } yield catalog
  implicit val dataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield ((title, author, isbn, description, coverImage, categories.toSet))

  "Adding new books to the catalog" >> {
    "indicates the books have been added to the catalog" >> {
      forAllNoShrink(catalogGenerator, dataGenerator) {
        (catalog: BookCatalogInterpreter, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val resultingCatalog =
                catalog.add(
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              resultingCatalog.isInstanceOf[Success[BookCatalog]]
          }
        }
      }
    }

    "places the books into the catalog" >> {
      forAllNoShrink(catalogGenerator, dataGenerator) {
        (catalog: BookCatalogInterpreter, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val resultingCatalog =
                catalog.add(
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              resultingCatalog match {
                case Success(updatedCatalog) =>
                  val addedBook =
                    updatedCatalog getByISBN isbn
                  addedBook.isInstanceOf[Success[Book]] &&
                  bookHasTitle(
                    addedBook,
                    title
                  ) &&
                  bookHasAuthor(
                    addedBook,
                    author
                  ) &&
                  bookHasISBN(
                    addedBook,
                    isbn
                  ) &&
                  bookHasDescription(
                    addedBook,
                    description
                  ) &&
                  bookHasCover(
                    addedBook,
                    coverImage
                  ) &&
                  bookHasCategories(
                    addedBook,
                    categories
                  )
                case Failure(_) =>
                  false
              }
          }
        }
      }
    }

    "gives new books to all listeners" >> {
      forAllNoShrink(catalogGenerator, dataGenerator) {
        (catalog: BookCatalogInterpreter, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              var sentBook: Book = null
              val updatedCatalog =
                catalog onAdd(
                  addedBook =>
                  sentBook = addedBook
                )
              val resultingCatalog =
                updatedCatalog.add(
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              resultingCatalog match {
                case Success(updatedCatalog) =>
                  sentBook != null &&
                  sentBook.title == title &&
                  sentBook.author == author &&
                  sentBook.isbn == isbn &&
                  sentBook.description == description &&
                  sentBook.coverImage == coverImage  &&
                  sentBook.categories == categories
                case Failure(_) =>
                  false
              }
          }
        }
      }
    }
  }

//  "Given a book catalog" - {
//    val testCatalog: BookCatalogInterpreter =
//      new BookCatalogInterpreter

//    "and a repository that contains the catalog" - {
//      val testRepository =
//        new FakeRepository

//      "and all of the information on a book to add to the catalog" - {
//        val newTitle =
//          "Ground Zero"
//        val newAuthor =
//          "Kevin J. Anderson"
//        val newISBN =
//          "006105223X"
//        val newDescription =
//          Some(
//            "Description for Ground Zero"
//          )
//        val newCover =
//          Some(
//            getClass().
//              getResource(
//                "/GroundZero.jpg"
//              ).toURI
//          )
//        val newCategories =
//          Set(
//            "sci-fi",
//            "conspiracy"
//          )

//        "and a listener for book addition events" - {
//          var sentBook: Book = null
//          testCatalog.onAdd(
//            addedBook => sentBook = addedBook
//          )

//          "when the book is added to the catalog" - {
//            val resultingBook =
//              testCatalog.add(
//                newTitle,
//                newAuthor,
//                newISBN,
//                newDescription,
//                newCover,
//                newCategories
//              )

//            "then the book is added to the catalog" in {
//              resultingBook should be (an[Try[_]])
//              resultingBook.get.title should be (newTitle)
//              resultingBook.get.author should be (newAuthor)
//              resultingBook.get.isbn should be (newISBN)
//              resultingBook.get.description should be (newDescription)
//              resultingBook.get.coverImage should be (newCover)
//              resultingBook.get.categories should be (newCategories)
//              testRepository.savedBook should be (resultingBook.get)
//            }

//            "and the book is given to the listener" in {
//              sentBook should not be (null)
//              sentBook.title should be (newTitle)
//              sentBook.author should be (newAuthor)
//              sentBook.isbn should be (newISBN)
//              sentBook.description should be (newDescription)
//              sentBook.coverImage should be (newCover)
//              sentBook.categories should be (newCategories)
//            }
//          }
//        }
//      }
//    }
//  }

//  "Given a book catalog" - {
//    val testCatalog: BookCatalogInterpreter =
//      new BookCatalogInterpreter

//    "and a repository that contains the catalog" - {
//      val testRepository =
//        new FakeRepository

//      "and the information on a book with no title" - {
//        val newTitle =
//          ""
//        val newAuthor =
//          "Kevin J. Anderson"
//        val newISBN =
//          "006105223X"
//        val newDescription =
//          Some(
//            "Description for Ground Zero"
//          )
//        val newCover =
//          Some(
//            getClass().
//              getResource(
//                "/GroundZero.jpg"
//              ).toURI
//          )
//        val newCategories =
//          Set(
//            "sci-fi",
//            "conspiracy"
//          )

//        "and a listener for book addition events" - {
//          var sentBook: Book = null
//          testCatalog.onAdd(
//            addedBook => {
//              sentBook = addedBook
//            }
//          )

//          "when the book information is attempted to be added to the catalog" - {
//            val resultingBook =
//              testCatalog.add(
//                newTitle,
//                newAuthor,
//                newISBN,
//                newDescription,
//                newCover,
//                newCategories
//              )

//            "then the book is not placed into the catalog" in {
//              resultingBook should be (a[Failure[_]])
//            }

//            "and the book is not given to the listener" in {
//              sentBook should be (null)
//            }
//          }
//        }
//      }
//    }
//  }

//  "Given a book catalog" - {
//    val testCatalog: BookCatalogInterpreter =
//      new BookCatalogInterpreter

//    "and a repository that contains the catalog" - {
//      val testRepository =
//        new FakeRepository

//      "and information on a book with no author" - {
//        val newTitle =
//          "Ground Zero"
//        val newAuthor =
//          ""
//        val newISBN =
//          "006105223X"
//        val newDescription =
//          Some(
//            "Description for Ground Zero"
//          )
//        val newCover =
//          Some(
//            getClass().
//              getResource(
//                "/GroundZero.jpg"
//              ).toURI
//          )
//        val newCategories =
//          Set(
//            "sci-fi",
//            "conspiracy"
//          )

//        "and a listener for book addition events" - {
//          var sentBook: Book = null
//          testCatalog.onAdd(
//            addedBook => sentBook = addedBook
//          )

//          "when the book information is attempted to be added to the catalog" - {
//            val resultingBook =
//              testCatalog.add(
//                newTitle,
//                newAuthor,
//                newISBN,
//                newDescription,
//                newCover,
//                newCategories
//              )

//            "then the book is not placed into the catalog" in {
//              resultingBook should be (a[Failure[_]])
//            }

//            "and the book is not given to the listener" in {
//              sentBook should be (null)
//            }
//          }
//        }
//      }
//    }
//  }

//  "Given a book catalog" - {
//    val testCatalog: BookCatalogInterpreter =
//      new BookCatalogInterpreter

//    "and a repository that contains the catalog" - {
//      val testRepository =
//        new FakeRepository

//      "and information on a book with no ISBN" - {
//        val newTitle =
//          "Ground Zero"
//        val newAuthor =
//          "Kevin J. Anderson"
//        val newISBN =
//          ""
//        val newDescription =
//          Some(
//            "Description for Ground Zero"
//          )
//        val newCover =
//          Some(
//            getClass().
//              getResource(
//                "/GroundZero.jpg"
//              ).toURI
//          )
//        val newCategories =
//          Set(
//            "sci-fi",
//            "conspiracy"
//          )

//        "and a listener for book addition events" - {
//          var sentBook: Book = null
//          testCatalog.onAdd(
//            addedBook => sentBook = addedBook
//          )

//          "when the book information is attempted to be added to the catalog" - {
//            val resultingBook =
//              testCatalog.add(
//                newTitle,
//                newAuthor,
//                newISBN,
//                newDescription,
//                newCover,
//                newCategories
//              )

//            "then the book is not placed into the catalog" in {
//              resultingBook should be (a[Failure[_]])
//            }

//            "and the book is not given to the listener" in {
//              sentBook should be (null)
//            }
//          }
//        }
//      }
//    }
//  }
  private def bookHasTitle(
    bookResult: Try[Book],
    expectedTitle: Titles
  ): Boolean =
    bookResult match {
      case Success(retrievedBook) =>
        retrievedBook.title == expectedTitle
      case Failure(_) =>
        false
    }

  private def bookHasAuthor(
    bookResult: Try[Book],
    expectedAuthor: Authors
  ): Boolean =
    bookResult match {
      case Success(retrievedBook) =>
        retrievedBook.author == expectedAuthor
      case Failure(_) =>
        false
    }

  private def bookHasISBN(
    bookResult: Try[Book],
    expectedISBN: ISBNs
  ): Boolean =
    bookResult match {
      case Success(retrievedBook) =>
        retrievedBook.isbn == expectedISBN
      case Failure(_) =>
        false
    }

  private def bookHasDescription(
    bookResult: Try[Book],
    expectedDescription: Description
  ): Boolean =
    bookResult match {
      case Success(retrievedBook) =>
        retrievedBook.description == expectedDescription
      case Failure(_) =>
        false
    }

  private def bookHasCover(
    bookResult: Try[Book],
    expectedCover: CoverImages
  ): Boolean =
    bookResult match {
      case Success(retrievedBook) =>
        retrievedBook.coverImage == expectedCover
      case Failure(_) =>
        false
    }

  private def bookHasCategories(
    bookResult: Try[Book],
    expectedCategories: Set[Categories]
  ) =
    bookResult match {
      case Success(retrievedBook) =>
        retrievedBook.categories == expectedCategories
      case Failure(_) =>
        false
    }
}
