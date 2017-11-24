package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service

import scala.util.Either

import cats.data.Validated
import Validated._

import org.scalacheck.{Gen, Arbitrary, Prop}
import Arbitrary.arbitrary
import Gen.const

import org.specs2.ScalaCheck
import org.specs2.matcher.Matcher
import org.specs2.mutable.Specification

import com.github.hobbitProg.dcm.matchers.bookCatalog.specs2.Conversions._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.
  BookCatalogServiceInterpreter
import BookCatalogServiceInterpreter._

/**
  * Specification for having the book catalog service adding a book
  * @author Kyle Cranmer
  * @since 0.2
  */
class AddingBookSpec
    extends Specification
    with ScalaCheck {
  sequential

  case class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book

  // Determine if result is valid
  def isValid[InvalidType, ValidType](
    result: Validated[InvalidType, ValidType]
  ) =
    result match {
      case Valid(_) => true
      case Invalid(_) => false
    }

  // Determine if result is invalid
  def isInvalid[InvalidType, ValidType](
    result: Validated[InvalidType, ValidType]
  ) =
    result match {
      case Valid(_) => false
      case Invalid(_) => true
    }

  // Matcher to determine if a result is valid
  def beValid[InvalidType, ValidType]: Matcher[Validated[InvalidType, ValidType]] = {
    result: Validated[InvalidType, ValidType] =>
    (
      isValid(result),
      "Result is valid",
      "Result is invalid"
    )
  }

  // Matcher to determine if a result is invalid
  def beInvalid[InvalidType, ValidType]: Matcher[Validated[InvalidType, ValidType]] = {
    result: Validated[InvalidType, ValidType] =>
    (
      isInvalid(result),
      "Result is invalid",
      "Result is valid"
    )
  }

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

  val repositoryGenerator = for {
    repository <- new FakeRepository
  } yield repository

  val dataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield (title, author, isbn, description, coverImage, categories.toSet)

  val noTitleGenerator = for {
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield ("", author, isbn, description, coverImage, categories.toSet)

  val noAuthorGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield (title, "", isbn, description, coverImage, categories.toSet)

  val noISBNGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield (title, author, "", description, coverImage, categories.toSet)

  "Adding a book with valid information to the catalog" >> {
    "indicates the book was added to the catalog" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, dataGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
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
              resultingCatalog must beValid
          }
        }
      }
    }

    "places the book into the catalog" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, dataGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
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
              resultingCatalog must containBook(TestBook(title, author, isbn, description, coverImage, categories))
          }
        }
      }
    }

    "places the book into the repository" >> {
      Prop.forAllNoShrink(catalogGenerator, repositoryGenerator, dataGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
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

              repository must haveBook(TestBook(title, author, isbn, description, coverImage, categories))
          }
        }
      }
    }
  }

  "Adding a book with no title to the catalog" >> {
    "indicates the book was not added to the catalog" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, noTitleGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
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
              resultingCatalog must beInvalid
          }
        }
      }
    }

    "does not place the book into the repository" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, noTitleGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
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
              repository must notHaveBook(TestBook(title, author, isbn, description, coverImage, categories))
          }
        }
      }
    }
  }

  "Adding a book with no author to the catalog " >> {
    "indicates the book was not added to the catalog" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, noAuthorGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
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
              resultingCatalog must beInvalid
          }
        }
      }
    }

    "does not place the book into the repository" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, noAuthorGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
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
              repository must notHaveBook(TestBook(title, author, isbn, description, coverImage, categories))
          }
        }
      }
    }
  }

  "Adding a book with no ISBN to the catalog" >> {
    "indicates the book was not added to the catalog" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, noISBNGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
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
              resultingCatalog must beInvalid
          }
        }
      }
    }

    "does not place the book into the repository" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, noISBNGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
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
              repository must notHaveBook(TestBook(title, author, isbn, description, coverImage, categories))
          }
        }
      }
    }
  }

  "Adding a book with the same title and author as a book in the catalog to " +
  "the catalog" >> {
    "indicates the book was not added to the catalog" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, dataGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              repository.existingTitle =
                title
              repository.existingAuthor =
                author
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
              resultingCatalog must beInvalid
          }
        }
      }
    }

    "does not place the book into the repository" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, dataGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              repository.existingTitle =
                title
              repository.existingAuthor =
                author
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
              repository must notHaveBook(TestBook(title, author, isbn, description, coverImage, categories))
          }
        }
      }
    }
  }

  "Adding a book with the same ISBN as a book in the catalog to the catalog" >> {
    "indicates the book was not added to the catalog" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, dataGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              repository.existingISBN =
                isbn
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
              resultingCatalog must beInvalid
          }
        }
      }
    }

    "does not place the book into the repository" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, dataGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              repository.existingISBN =
                isbn
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
              repository must notHaveBook(TestBook(title, author, isbn, description, coverImage, categories))
          }
        }
      }
    }
  }
}
