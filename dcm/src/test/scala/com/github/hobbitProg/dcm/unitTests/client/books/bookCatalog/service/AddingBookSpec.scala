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

import com.github.hobbitProg.dcm.matchers.bookCatalog.Conversions._

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

  def isValid[InvalidType, ValidType](
    result: Validated[InvalidType, ValidType]
  ) = {
    result match {
      case Valid(_) => true
      case Invalid(_) => false
    }
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

  "Given a valid book information to add to the catalog" >> {
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

  "Given book information without a title" >> {
    "indicates the book was not added to the catalog" >> pending
    "does not place the book into the catalog" >> pending
    "does not place the book into the repository" >> pending
  }

  "Given book information without an author" >> {
    "indicates the book was not added to the catalog" >> pending
    "does not place the book into the catalog" >> pending
    "does not place the book into the repository" >> pending
  }

  "Given book information without an ISBN" >> {
    "indicates the book was not added to the catalog" >> pending
    "does not place the book into the catalog" >> pending
    "does not place the book into the repository" >> pending
  }

  "Given book information with a title and author of a book that already " +
  "exists in the catalog" >> {
    "indicates the book was not added to the catalog" >> pending
    "does not place the book into the catalog" >> pending
    "does not place the book into the repository" >> pending
  }

  "Given book information with an ISBN of a book that already exists in the " +
  "catalog" >> {
    "indicates the book was not added to the catalog" >> pending
    "does not place the book into the catalog" >> pending
    "does not place the book into the repository" >> pending
  }
}
