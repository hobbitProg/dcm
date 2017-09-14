package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model

import scala.util.{Try, Success, Failure}

import org.scalacheck.{Gen, Arbitrary, Prop}
import Arbitrary.arbitrary

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

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
  private type CatalogInfoType = (BookCatalog, Titles, Authors, ISBNs, Description, CoverImages, Set[Categories])
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

  val titleGenerator = for (
    title <- arbitrary[String].suchThat(_.length > 0)
  ) yield title

  "When modifying a the title of an existing book" >> {
    "the book with the new title is placed into the catalog" >> {
      Prop.forAllNoShrink(catalogGenerator, titleGenerator) {
        (catalogData: Try[CatalogInfoType], newTitle: Titles) => {
          catalogData match {
            case Success((catalog, title, author, isbn, description, coverImage, categories)) =>
              getByISBN(
                catalog,
                isbn
              ) match {
                case Success(originalBook) =>
                  val updatedCatalog =
                    updateBook(
                      catalog,
                      originalBook,
                      newTitle,
                      author,
                      isbn,
                      description,
                      coverImage,
                      categories
                    )
                  updatedCatalog.isInstanceOf[Success[BookCatalog]] &&
                  bookDataMatches(
                    updatedCatalog,
                    isbn,
                    retrievedBook =>
                    retrievedBook.title == newTitle
                  ) &&
                  bookDataMatches(
                    updatedCatalog,
                    isbn,
                    retrievedBook =>
                    retrievedBook.author == author
                  ) &&
                  bookDataMatches(
                    updatedCatalog,
                    isbn,
                    retrievedBook =>
                    retrievedBook.isbn == isbn
                  ) &&
                  bookDataMatches(
                    updatedCatalog,
                    isbn,
                    retrievedBook =>
                    retrievedBook.description == description
                  ) &&
                  bookDataMatches(
                    updatedCatalog,
                    isbn,
                    retrievedBook =>
                    retrievedBook.coverImage == coverImage
                  ) &&
                  bookDataMatches(
                    updatedCatalog,
                    isbn,
                    retrievedBook =>
                    retrievedBook.categories == categories
                  )
                case Failure(_) =>
                  false
              }
            case Failure(_) =>
              false
          }
        }
      }
    }

    "the modified book is given to all listeners" >> {
      Prop.forAllNoShrink(catalogGenerator, titleGenerator) {
        (catalogData: Try[CatalogInfoType], newTitle: Titles) => {
          catalogData match {
            case Success((catalog, title, author, isbn, description, coverImage, categories)) =>
              var givenUpdatedBook: Book = null
              val catalogWithSubscriber =
                onModify(
                  catalog,
                  (_, updatedBook) =>
                  givenUpdatedBook = updatedBook
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
                  ) match {
                    case Success(updatedCatalog) =>
                      getByISBN(
                        updatedCatalog,
                        isbn
                      ) match {
                        case Success(retrievedBook) =>
                          retrievedBook == givenUpdatedBook
                        case Failure(_) =>
                          false
                      }
                    case Failure(_) =>
                      false
                  }
                case Failure(_) =>
                  false
              }
            case Failure(_) =>
              false
          }
        }
      }
    }

    "the original book was removed from the catalog" >> pending
    "the original book is given to all listeners" >> pending
  }

  def bookDataMatches(
    catalogResult: Try[BookCatalog],
    expectedISBN: ISBNs,
    bookPredicate: Book => Boolean
  ): Boolean = {
    catalogResult match {
      case Success(resultingCatalog) =>
        getByISBN(
          resultingCatalog,
          expectedISBN
        ) match {
          case Success(retrievedBook) =>
            bookPredicate(
              retrievedBook
            )
          case Failure(_) =>
            false
        }
      case Failure(_) =>
        false
    }
  }
}
