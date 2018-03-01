package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.modifyingBook

import scala.collection.Set
import scala.util.{Try, Success, Failure}

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop._

import org.scalacheck.{Gen, Arbitrary}
import Arbitrary.arbitrary
import Gen.const

import com.github.hobbitProg.dcm.matchers.bookCatalog.scalatest._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Specification for modifying an ISBN of a book that exists in the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingISBNOfBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with CatalogMatchers {

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

  private val isbnGenerator = for {
    isbn <- arbitrary[String].suchThat(_.length > 0)
  } yield isbn

  // Modify the ISBN of a book in the catalog
  private def modifyISBNOfBook(
    catalogData: Try[CatalogInfoType],
    newISBN: ISBNs
  ) : Try[BookCatalog] = {
    val Success((catalog, title, author, isbn, description, coverImage, categories)) =
      catalogData
    val catalogWithSubscriber =
      onModify(
        catalog,
        (originalBook, updatedBook) => {
          givenOriginalBook = originalBook
          givenUpdatedBook = updatedBook
        }
      )
    val Success(originalBook) =
      getByISBN(
        catalogWithSubscriber,
        isbn
      )
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
  }

  property("the book with the new ISBN is placed into the catalog") {
    forAll(catalogGenerator, isbnGenerator) {
      (catalogData: Try[CatalogInfoType], newISBN: ISBNs) =>
      val Success((catalog, title, author, isbn, description, coverImage, categories)) =
        catalogData
      modifyISBNOfBook(
        catalogData,
        newISBN
      ) should containBook(
        TestBook(
          title,
          author,
          newISBN,
          description,
          coverImage,
          categories
        )
      )
    }
  }

  property("the original book is given to all listeners") {
    forAll(catalogGenerator, isbnGenerator) {
      (catalogData: Try[CatalogInfoType], newISBN: ISBNs) =>
      modifyISBNOfBook(
        catalogData,
        newISBN
      )
      val Success((catalog, title, author, isbn, description, coverImage, categories)) =
        catalogData
      val originalBook =
        TestBook(
          title,
          author,
          isbn,
          description,
          coverImage,
          categories
        )
      givenOriginalBook should equal (originalBook)
    }
  }

  property("the updated book is given to all listeners") {
    forAll(catalogGenerator, isbnGenerator) {
      (catalogData: Try[CatalogInfoType], newISBN: ISBNs) =>
      modifyISBNOfBook(
        catalogData,
        newISBN
      )
      val Success((catalog, title, author, isbn, description, coverImage, categories)) =
        catalogData
      val updatedBook =
        TestBook(
          title,
          author,
          newISBN,
          description,
          coverImage,
          categories
        )
      givenUpdatedBook should equal (updatedBook)
    }
  }
}
