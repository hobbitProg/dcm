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
import BookCatalog._

/**
  * Verifies books can be added to catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
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
  val catalogGenerator = for {
    catalog <- new BookCatalog
  } yield catalog

  val dataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield ((title, author, isbn, description, coverImage, categories.toSet))

  val emptyTitleDataGenerator = for {
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield (("", author, isbn, description, coverImage, categories.toSet))

  val emptyAuthorDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield((title, "", isbn, description, coverImage, categories.toSet))

  val emptyISBNDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield((title, author, "", description, coverImage, categories.toSet))

  "Adding new books to the catalog" >> {
    "indicates the books have been added to the catalog" >> {
      forAllNoShrink(catalogGenerator, dataGenerator) {
        (catalog: BookCatalog, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val resultingCatalog =
                addBook(
                  catalog,    
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
        (catalog: BookCatalog, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val resultingCatalog =
                addBook(
                  catalog,
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
                    getByISBN(
                      updatedCatalog,
                      isbn
                    )
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
        (catalog: BookCatalog, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              var sentBook: Book = null
              val updatedCatalog =
                onAdd(
                  catalog,
                  addedBook =>
                  sentBook = addedBook
                )
              val resultingCatalog =
                addBook(
                  updatedCatalog,
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

  "Attempting to add books with no title to the catalog" >> {
    "no book is placed into the catalog" >> {
      forAllNoShrink(catalogGenerator, emptyTitleDataGenerator) {
        (catalog: BookCatalog, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val resultingCatalog =
                addBook(
                  catalog,
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              resultingCatalog.isInstanceOf[Failure[_]]
          }
        }
      }
    }

    "no book is given to the listener" >> {
      forAllNoShrink(catalogGenerator, emptyTitleDataGenerator) {
        (catalog: BookCatalog, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              var sentBook: Book = null
              val updatedCatalog =
                onAdd(
                  catalog,
                  addedBook =>
                  sentBook = addedBook
                )

              val resultingCatalog =
                addBook(
                  catalog,
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )

              sentBook == null
          }
        }
      }
    }
  }

  "Attempting to add books with no author to the catalog" >> {
    "no book is added to the catalog" >> {
      forAllNoShrink(catalogGenerator, emptyAuthorDataGenerator) {
        (catalog: BookCatalog, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val resultingCatalog =
                addBook(
                  catalog,
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              resultingCatalog.isInstanceOf[Failure[_]]
          }
        }
      }
    }
    "no book is given to the listener" >> {
      forAllNoShrink(catalogGenerator, emptyAuthorDataGenerator) {
        (catalog: BookCatalog, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              var sentBook: Book = null
              val updatedCatalog =
                onAdd(
                  catalog,
                  addedBook =>
                  sentBook = addedBook
                )

              val resultingCatalog =
                addBook(
                  catalog,
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )

              sentBook == null
          }
        }
      }
    }
  }

  "Attempting to add books with no ISBN to the catalog" >> {
    "no book is added to the catalog" >> {
      forAllNoShrink(catalogGenerator, emptyISBNDataGenerator) {
        (catalog: BookCatalog, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val resultingCatalog =
                addBook(
                  catalog,
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )

              resultingCatalog.isInstanceOf[Failure[_]]
          }
        }
      }
    }

    "no book is given to the listener" >> {
      forAllNoShrink(catalogGenerator, emptyISBNDataGenerator) {
        (catalog: BookCatalog, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              var sentBook: Book = null
              val updatedCatalog =
                onAdd(
                  catalog,
                  addedBook =>
                  sentBook = addedBook
                )

              val resultingCatalog =
                addBook(
                  catalog,
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )

              sentBook == null
          }
        }
      }
    }
  }

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
