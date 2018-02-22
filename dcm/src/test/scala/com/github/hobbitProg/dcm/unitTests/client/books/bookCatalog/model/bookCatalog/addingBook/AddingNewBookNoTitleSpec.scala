package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.addingBook

import scala.util.{Success, Failure}

import org.scalatest.{PropSpec, Matchers}
import org.scalatest.prop._

import org.scalacheck.{Gen, Arbitrary, Prop}
import Arbitrary.arbitrary
import Gen.const

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Specification for trying to add a book with no title
  * @author Kyle Cranmer
  * @since 0.2
  */
class AddingNewBookNoTitleSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers {

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

  val emptyTitleDataGenerator = for {
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield (("", author, isbn, description, coverImage, categories.toSet))

  property("no book is placed into the catalog") {
    forAll(catalogGenerator, emptyTitleDataGenerator) {
      (catalog: BookCatalog, data: BookDataType) =>
      data match {
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
          resultingCatalog shouldBe a [Failure[_]]
      }
    }
  }

  property("no book is given to the listener") {
    forAll(catalogGenerator, emptyTitleDataGenerator) {
      (catalog: BookCatalog, data: BookDataType) =>
      data match {
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
          sentBook shouldBe null
      }
    }
  }
}
