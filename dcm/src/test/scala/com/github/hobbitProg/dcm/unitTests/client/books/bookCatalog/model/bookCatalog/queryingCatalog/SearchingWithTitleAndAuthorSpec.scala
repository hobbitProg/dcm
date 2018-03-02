package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.queryingCatalog

import scala.List
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
  * Specification for querying the book catalog using the title and
  * author of a book
  * @author Kyle Cranmer
  * @since 0.2
  */
class SearchingWithTitleAndAuthorSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers {

  private type BookDataType = (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories])

  private type TitleAuthorQueryDataType = (List[BookDataType], Titles, Authors)

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

  val successfulTitleAuthorMatchGenerator = for {
    existingBookData <- Gen.listOfN(25, dataGenerator)
    dataToQuery <- Gen.oneOf(existingBookData)
  } yield (existingBookData, dataToQuery._1, dataToQuery._2)

  val unsuccessfulTitleAuthorMatchGenerator = for {
    existingBookData <- Gen.listOfN(26, dataGenerator)
  } yield (existingBookData.drop(1), existingBookData.head._1, existingBookData.head._2)

  // Populate the test catalog
  private def populateCatalog(
    catalog: BookCatalog,
    availableBooks: List[BookDataType]
  ) : BookCatalog =
    availableBooks.foldLeft(
      catalog
    ){
      (catalogBeingPopulated, currentBookData) =>
      currentBookData match {
        case (title, author, isbn, description, coverImage,
          categories) =>
          addBook(
            catalogBeingPopulated,
            title,
            author,
            isbn,
            description,
            coverImage,
            categories
          ).get
      }
    }

  property("indicates when a book in the catalog has the given title " +
    "and author") {
    forAll(catalogGenerator, successfulTitleAuthorMatchGenerator) {
      (catalog: BookCatalog, queryData: TitleAuthorQueryDataType) =>
      queryData match {
        case (availableBooks, matchingTitle, matchingAuthor) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              availableBooks
            )
          exists(
            populatedCatalog,
            matchingTitle,
            matchingAuthor
          ) should be (true)
      }
    }
  }

  property("indicates when no book in the catalog has the given " +
    "title and author") {
    forAll(catalogGenerator, unsuccessfulTitleAuthorMatchGenerator) {
      (catalog: BookCatalog, queryData: TitleAuthorQueryDataType) =>
      queryData match {
        case (availableBooks, differentTitle, differentAuthor) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              availableBooks
            )
          exists(
            populatedCatalog,
            differentTitle,
            differentAuthor
          ) should be (false)
      }
    }
  }
}
