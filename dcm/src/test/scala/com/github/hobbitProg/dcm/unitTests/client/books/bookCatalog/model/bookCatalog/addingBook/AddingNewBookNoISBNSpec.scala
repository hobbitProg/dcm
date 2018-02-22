package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.addingBook

import scala.collection.Set
import scala.util.Failure

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop._

import org.scalacheck.{Gen, Arbitrary}
import Arbitrary.arbitrary
import Gen.const

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Specification for trying to add a new book to the catalog with no ISBN
  * @author Kyle Cranmer
  * @since 0.2
  */
class AddingNewBookNoISBNSpec
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

  val emptyISBNDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield((title, author, "", description, coverImage, categories.toSet))

  property("no book is added to the catalog") {
    forAll(catalogGenerator, emptyISBNDataGenerator) {
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
            resultingCatalog shouldBe a [Failure[_]]
        }
      }
    }
  }

  property("no book is given to the listener") {
    forAll(catalogGenerator, emptyISBNDataGenerator) {
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
            sentBook shouldBe null
        }
      }
    }
  }
}
