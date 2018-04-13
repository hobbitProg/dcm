package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.addingBook

import scala.collection.Set
import scala.util.Success

import org.scalatest.{Matchers, PropSpec, TryValues}
import org.scalatest.prop._

import org.scalacheck.{Gen, Arbitrary}
import Arbitrary.arbitrary
import Gen.const

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Specification for successfully adding new books to the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class AddingNewBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with TryValues
    with Matchers {
  case class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book {
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

  val dataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield ((title, author, isbn, description, coverImage, categories.toSet))

  property("indicates the catalog was updated") {
    forAll(catalogGenerator, dataGenerator) {
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
          resultingCatalog should be a 'success
      }
    }
  }

  property("places the books into the catalog") {
    forAll(catalogGenerator, dataGenerator) {
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
          val addedBook =
            for {
              resultingCatalog <- addBook(catalog, title, author, isbn, description, coverImage, categories)
              retrievedBook <- getByISBN(resultingCatalog, isbn)
            } yield retrievedBook
          addedBook should be (Success(TestBook(title, author, isbn, description, coverImage, categories)))
      }
    }
  }

  property("gives new books to all listeners") {
    forAll(catalogGenerator, dataGenerator) {
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
          sentBook should be (TestBook(title, author, isbn, description, coverImage, categories))
      }
    }
  }
}
