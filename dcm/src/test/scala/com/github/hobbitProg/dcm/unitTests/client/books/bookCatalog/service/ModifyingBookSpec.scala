package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service

import scala.util.{Success, Failure}

import cats.data.Validated
import Validated._

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

import org.scalacheck.{Gen, Arbitrary, Prop}
import Arbitrary.arbitrary
import Gen.const

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.
  BookCatalogServiceInterpreter
import BookCatalogServiceInterpreter._

/**
  * Specification for having the book catalog service modify a book
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingBookSpec
    extends Specification
    with ScalaCheck {
  sequential

  private type TitleModificationType =
    (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories], Titles)

  case class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book

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
  
  val repositoryGenerator = for {
    repository <- new FakeRepository
  } yield repository

  val titleModificationGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
    newTitle <- arbitrary[String].suchThat(generatedTitle => generatedTitle != title && generatedTitle.length > 0)
  } yield (title, author, isbn, description, coverImage, categories.toSet, newTitle)

  "Changing the title of a book in the catalog to a new title that does not " +
  "exist in the catalog" >> {
    "indicates the catalog was updated" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, titleModificationGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: TitleModificationType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories, newTitle) =>
              val resultingCatalog =
                insertBook(
                  catalog,
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )(
                  repository
                )
              resultingCatalog match {
                case Valid(populatedCatalog) =>
                  val queryResult =
                    getByISBN(
                      populatedCatalog,
                      isbn
                    )
                  queryResult match {
                    case Success(originalBook) =>
                      val updatedCatalog =
                        modifyBook(
                          populatedCatalog,
                          originalBook,
                          newTitle,
                          author,
                          isbn,
                          description,
                          coverImage,
                          categories
                        )(
                          repository
                        )
                      updatedCatalog match {
                        case Valid(_) => true
                        case Invalid(_) => false
                      }
                    case Failure(_) => false
                  }
                case Invalid(_) => false
              }
          }
        }
      }
    }

    "places the updated book in the catalog" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, titleModificationGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: TitleModificationType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories, newTitle) =>
              val resultingCatalog =
                insertBook(
                  catalog,
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )(
                  repository
                )
              resultingCatalog match {
                case Valid(populatedCatalog) =>
                  val queryResult =
                    getByISBN(
                      populatedCatalog,
                      isbn
                    )
                  queryResult match {
                    case Success(originalBook) =>
                      val updatedCatalog =
                        modifyBook(
                          populatedCatalog,
                          originalBook,
                          newTitle,
                          author,
                          isbn,
                          description,
                          coverImage,
                          categories
                        )(
                          repository
                        )
                      updatedCatalog match {
                        case Valid(modifiedCatalog) =>
                          val updatedResult =
                            getByISBN(
                              modifiedCatalog,
                              isbn
                            )
                          updatedResult match {
                            case Success(updatedBook) =>
                              updatedBook ==
                              TestBook(
                                newTitle,
                                author,
                                isbn,
                                description,
                                coverImage,
                                categories
                              )
                            case Failure(_) => false
                          }
                        case Invalid(_) => false
                      }
                    case Failure(_) => false
                  }
                case Invalid(_) => false
              }
          }
        }
      }
    }

    "places the updated book in the repository" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, titleModificationGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: TitleModificationType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories, newTitle) =>
              val resultingCatalog =
                insertBook(
                  catalog,
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )(
                  repository
                )
              resultingCatalog match {
                case Valid(populatedCatalog) =>
                  val queryResult =
                    getByISBN(
                      populatedCatalog,
                      isbn
                    )
                  queryResult match {
                    case Success(originalBook) =>
                      val updatedCatalog =
                        modifyBook(
                          populatedCatalog,
                          originalBook,
                          newTitle,
                          author,
                          isbn,
                          description,
                          coverImage,
                          categories
                        )(
                          repository
                        )
                      updatedCatalog match {
                        case Valid(_) =>
                          repository.retrieve(isbn) match {
                            case Right(updatedBook) =>
                              updatedBook ==
                              TestBook(
                                newTitle,
                                author,
                                isbn,
                                description,
                                coverImage,
                                categories
                              )
                            case Left(_) => false
                          }
                        case Invalid(_) => false
                      }
                    case Failure(_) => false
                  }
                case Invalid(_) => false
              }
          }
        }
      }
    }

    "gives the updated book to the listener" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, titleModificationGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: TitleModificationType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories, newTitle) =>
              val resultingCatalog =
                insertBook(
                  catalog,
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )(
                  repository
                )
              resultingCatalog match {
                case Valid(populatedCatalog) =>
                  val queryResult =
                    getByISBN(
                      populatedCatalog,
                      isbn
                    )
                  queryResult match {
                    case Success(originalBook) =>
                      var givenUpdatedBook: Book = null
                      val catalogWithSubscriber =
                        onModify(
                          populatedCatalog,
                          (_, updatedBook) =>
                          givenUpdatedBook = updatedBook
                        )
                      val updatedCatalog =
                        modifyBook(
                          catalogWithSubscriber,
                          originalBook,
                          newTitle,
                          author,
                          isbn,
                          description,
                          coverImage,
                          categories
                        )(
                          repository
                        )
                      updatedCatalog match {
                        case Valid(_) =>
                          givenUpdatedBook ==
                          TestBook(
                            newTitle,
                            author,
                            isbn,
                            description,
                            coverImage,
                            categories
                          )
                        case Invalid(_) => false
                      }
                    case Failure(_) => false
                  }
                case Invalid(_) => false
              }
          }
        }
      }
    }

    "removes the original book from the catalog" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, titleModificationGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: TitleModificationType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories, newTitle) =>
              val resultingCatalog =
                insertBook(
                  catalog,
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )(
                  repository
                )
              resultingCatalog match {
                case Valid(populatedCatalog) =>
                  val queryResult =
                    getByISBN(
                      populatedCatalog,
                      isbn
                    )
                  queryResult match {
                    case Success(originalBook) =>
                      val updatedCatalog =
                        modifyBook(
                          populatedCatalog,
                          originalBook,
                          newTitle,
                          author,
                          isbn,
                          description,
                          coverImage,
                          categories
                        )(
                          repository
                        )
                      updatedCatalog match {
                        case Valid(modifiedCatalog) =>
                          val updatedResult =
                            getByISBN(
                              modifiedCatalog,
                              isbn
                            )
                          updatedResult match {
                            case Success(updatedBook) =>
                              updatedBook !=
                              originalBook
                            case Failure(_) => false
                          }
                        case Invalid(_) => false
                      }
                    case Failure(_) => false
                  }
                case Invalid(_) => false
              }
          }
        }
      }
    }

    "removes the original book from the repository" >> pending
    "gives the original book to the listener" >> pending
  }
}
