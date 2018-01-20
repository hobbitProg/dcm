package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model

import scala.util.{Try, Success, Failure}

import org.scalacheck.{Gen, Arbitrary, Prop}
import Arbitrary.arbitrary

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

import com.github.hobbitProg.dcm.matchers.bookCatalog.specs2.Conversions._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Verifies books in catalog can be modified
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyBookSpec
    extends Specification
    with ScalaCheck {

  sequential

  private case class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book {
  }

  private type CatalogInfoType = (BookCatalog, Titles, Authors, ISBNs, Description, CoverImages, Set[Categories])

  private var givenOriginalBook: Book = null
  private var givenUpdatedBook: Book = null

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

  private val catalogGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
    catalog <- addBook(
      new BookCatalog(),
      title,
      author,
      isbn,
      description,
      coverImage,
      categories.toSet
    )
  } yield (catalog, title, author, isbn, description, coverImage, categories.toSet)

  private val titleGenerator = for (
    title <- arbitrary[String].suchThat(_.length > 0)
  ) yield title

  private val authorGenerator = for (
    author <- arbitrary[String].suchThat(_.length > 0)
  ) yield author

  private val isbnGenerator = for {
    isbn <- arbitrary[String].suchThat(_.length > 0)
  } yield isbn

  // Modify the title of a book in the catalog
  private def modifyTitleOfBook(
    catalogData: Try[CatalogInfoType],
    newTitle: Titles
  ) : Try[BookCatalog] =
    catalogData match {
      case Success((catalog, title, author, isbn, description, coverImage, categories)) =>
        val catalogWithSubscriber =
          onModify(
            catalog,
            (originalBook, updatedBook) => {
              givenOriginalBook = originalBook
              givenUpdatedBook = updatedBook
            }
          )
        getByISBN(
          catalogWithSubscriber,
          isbn
        ) match {
          case Success(originalBook) =>
            updateBook(
              catalogWithSubscriber,
              originalBook,
              newTitle,
              author,
              isbn,
              description,
              coverImage,
              categories
            )
          case Failure(errorMessage) =>
            Failure(errorMessage)
        }
      case Failure(errorMessage) =>
        Failure(errorMessage)

    }

  // Modify the author of a book in the catalog
  private def modifyAuthorOfBook(
    catalogData: Try[CatalogInfoType],
    newAuthor: Authors
  ) : Try[BookCatalog] =
    catalogData match {
      case Success((catalog, title, author, isbn, description, coverImage, categories)) =>
        val catalogWithSubscriber =
          onModify(
            catalog,
            (originalBook, updatedBook) => {
              givenOriginalBook = originalBook
              givenUpdatedBook = updatedBook
            }
          )
        getByISBN(
          catalogWithSubscriber,
          isbn
        ) match {
          case Success(originalBook) =>
            updateBook(
              catalogWithSubscriber,
              originalBook,
              title,
              newAuthor,
              isbn,
              description,
              coverImage,
              categories
            )
          case Failure(errorMessage) =>
            Failure(errorMessage)
        }
      case Failure(errorMessage) =>
        Failure(errorMessage)
    }

  // Modify the ISBN of a book in the catalog
  private def modifyISBNOfBook(
    catalogData: Try[CatalogInfoType],
    newISBN: ISBNs
  ) : Try[BookCatalog] =
    catalogData match {
      case Success((catalog, title, author, isbn, description, coverImage, categories)) =>
        val catalogWithSubscriber =
          onModify(
            catalog,
            (originalBook, updatedBook) => {
              givenOriginalBook = originalBook
              givenUpdatedBook = updatedBook
            }
          )
        getByISBN(
          catalogWithSubscriber,
          isbn
        ) match {
          case Success(originalBook) =>
            updateBook(
              catalogWithSubscriber,
              originalBook,
              title,
              author,
              newISBN,
              description,
              coverImage,
              categories
            )
          case Failure(errorMessage) =>
            Failure(errorMessage)
        }
      case Failure(errorMessage) =>
        Failure(errorMessage)
    }

  "When modifying the title of an existing book" >> {
    "the book with the new title is placed into the catalog" >> {
      Prop.forAllNoShrink(catalogGenerator, titleGenerator) {
        (catalogData: Try[CatalogInfoType], newTitle: Titles) => {
          catalogData match {
            case Success((catalog, title, author, isbn, description, coverImage, categories)) =>
              modifyTitleOfBook(
                catalogData,
                newTitle
              ) must containModifiedBook(
                newTitle,
                author,
                isbn,
                description,
                coverImage,
                categories
              )
            case Failure(_) =>
              catalogData must beASuccessfulTry
          }
        }
      }
    }

    "the modified book is given to all listeners" >> {
      Prop.forAllNoShrink(catalogGenerator, titleGenerator) {
        (catalogData: Try[CatalogInfoType], newTitle: Titles) => {
          modifyTitleOfBook(
            catalogData,
            newTitle
          )
          catalogData match {
            case Success((catalog, title, author, isbn, description, coverImage, categories)) =>
              val expectedBook =
                TestBook(
                  newTitle,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              givenUpdatedBook must beEqualTo(expectedBook)
            case Failure(_) =>
              catalogData must beASuccessfulTry
          }
        }
      }
    }

    "the original book is given to all listeners" >> {
      Prop.forAllNoShrink(catalogGenerator, titleGenerator) {
        (catalogData: Try[CatalogInfoType], newTitle: Titles) => {
          modifyTitleOfBook(
            catalogData,
            newTitle
          )
          catalogData match {
            case Success((catalog, title, author, isbn, description, coverImage, categories)) =>
              val originalBook =
                TestBook(
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              givenOriginalBook must beEqualTo(originalBook)
            case Failure(_) =>
              catalogData must beASuccessfulTry
          }
        }
      }
    }
  }

  "When modifying the author of an existing book" >> {
    "the book with the new author is placed into the catalog" >> {
      Prop.forAllNoShrink(catalogGenerator, authorGenerator) {
        (catalogData: Try[CatalogInfoType], newAuthor: Authors) =>
        catalogData match {
          case Success((catalog, title, author, isbn, description, coverImage, categories)) =>
            modifyAuthorOfBook(
              catalogData,
              newAuthor
            ) must containModifiedBook(
              title,
              newAuthor,
              isbn,
              description,
              coverImage,
              categories)
          case Failure(_) =>
            catalogData must beASuccessfulTry
        }
      }
    }

    "the original book is given to all listeners" >> {
      Prop.forAllNoShrink(catalogGenerator, authorGenerator) {
        (catalogData: Try[CatalogInfoType], newAuthor: Authors) => {
          modifyAuthorOfBook(
            catalogData,
            newAuthor
          )
          catalogData match {
            case Success((catalog, title, author, isbn, description, coverImage, categories)) =>
              val expectedBook =
                TestBook(
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              givenOriginalBook must beEqualTo(expectedBook)
            case Failure(_) =>
              catalogData must beASuccessfulTry
          }
        }
      }
    }

    "the modified book is given to all listeners" >> {
      Prop.forAllNoShrink(catalogGenerator, authorGenerator) {
        (catalogData: Try[CatalogInfoType], newAuthor: Authors) => {
          modifyAuthorOfBook(
            catalogData,
            newAuthor
          )
          catalogData match {
            case Success((catalog, title, author, isbn, description, coverImage, categories)) =>
              val expectedBook =
                TestBook(
                  title,
                  newAuthor,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              givenUpdatedBook must beEqualTo(expectedBook)
            case Failure(_) =>
              catalogData must beASuccessfulTry
          }
        }
      }
    }
  }

  "When modifying the ISBN of an existing book" >> {
    "the book with the new ISBN is placed into the catalog" >> {
      Prop.forAllNoShrink(catalogGenerator, isbnGenerator) {
        (catalogData: Try[CatalogInfoType], newISBN: ISBNs) => {
          catalogData match {
            case Success((catalog, title, author, isbn, description, coverImage, categories)) =>
              modifyISBNOfBook(
                catalogData,
                newISBN
              ) must containModifiedBook(
                title,
                author,
                newISBN,
                description,
                coverImage,
                categories
              )
            case Failure(_) =>
              catalogData must beASuccessfulTry
          }
        }
      }
    }

    "the original book is given to all listeners" >> {
      Prop.forAllNoShrink(catalogGenerator, isbnGenerator) {
        (catalogData: Try[CatalogInfoType], newISBN: ISBNs) => {
          modifyISBNOfBook(
            catalogData,
            newISBN
          )
          catalogData match {
            case Success((catalog, title, author, isbn, description, coverImage, categories)) =>
              val originalBook =
                TestBook(
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              givenOriginalBook must beEqualTo(originalBook)
            case Failure(_) =>
              catalogData must beASuccessfulTry
          }
        }
      }
    }

    "the modified book is given to all listeners" >> {
      Prop.forAllNoShrink(catalogGenerator, isbnGenerator) {
        (catalogData: Try[CatalogInfoType], newISBN: ISBNs) => {
          modifyISBNOfBook(
            catalogData,
            newISBN
          )
          catalogData match {
            case Success((catalog, title, author, isbn, description, coverImage, categories)) =>
              val expectedBook =
                TestBook(
                  title,
                  author,
                  newISBN,
                  description,
                  coverImage,
                  categories
                )
              givenUpdatedBook must beEqualTo(expectedBook)
            case Failure(_) =>
              catalogData must beASuccessfulTry
          }
        }
      }
    }
  }

  "When modifying the description of an existing book" >> {
    "the book with the new description is placed into the catalog" >> pending
    "the original book is given to all listeners" >> pending
    "the modified book is given to all listeners" >> pending
  }

  "When modifying the cover image of an existing book" >> {
    "the book with the new cover image is placed into the catalog" >> pending
    "the original book is given to all listeners" >> pending
    "the modified book is given to all listeners" >> pending
  }

  "When modifying the categories of an existing book" >> {
    "the book with the new categories is placed into the catalog" >> pending
    "the original book is given to all listeners" >> pending
    "the modified book is given to all listeners" >> pending
  }

  "When removing the title of a book" >> {
    "no book is placed into the catalog" >> pending
    "no book is fiven to the listener" >> pending
  }
}
