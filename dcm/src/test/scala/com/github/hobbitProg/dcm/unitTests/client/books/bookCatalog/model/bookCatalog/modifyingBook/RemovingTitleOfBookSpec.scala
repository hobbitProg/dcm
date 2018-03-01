package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.modifyingBook

import scala.collection.Set
import scala.util.{Try, Success, Failure}

import org.scalatest.{Matchers, PropSpec, TryValues}
import org.scalatest.prop._

import org.scalacheck.{Gen, Arbitrary}
import Arbitrary.arbitrary
import Gen.const

import com.github.hobbitProg.dcm.matchers.bookCatalog.scalatest._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Specification for removing a title of a book that exists in the
  * catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class RemovingTitleOfBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with CatalogMatchers
    with TryValues {

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

  // Modify the title of a book in the catalog
  private def modifyTitleOfBook(
    catalogData: Try[CatalogInfoType],
    newTitle: Titles
  ) : Try[BookCatalog] = {
    val Success(
      (
        catalog,
        title,
        author,
        isbn,
        description,
        coverImage,
        categories
      )
    ) = catalogData
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
      newTitle,
      author,
      isbn,
      description,
      coverImage,
      categories
    )
  }

  property("an indication that an error occurred is generated") {
    forAll (catalogGenerator) {
      (catalogData: Try[CatalogInfoType]) =>
      val Success(
        (
          catalog,
          title,
          author,
          isbn,
          description,
          coverImage,
          categories
        )
      ) = catalogData
      modifyTitleOfBook(
        catalogData,
        ""
      ) should be a 'failure
    }
  }

  property("no book is given to the listener"){
    forAll (catalogGenerator) {
      (catalogData: Try[CatalogInfoType]) =>
      val Success(
        (
          catalog,
          title,
          author,
          isbn,
          description,
          coverImage,
          categories
        )
      ) = catalogData
      modifyTitleOfBook(
        catalogData,
        ""
      )
      givenOriginalBook should equal (null)
      givenUpdatedBook should equal (null)
    }
  }
}
