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
  * Specification for querying the book catalog using the ISBN of a
  * book
  * @author Kyle Cranmer
  * @since 0.2
  */
class SearchingWithISBNSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers {

  private type BookDataType = (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories])

  private type ISBNQueryDataType = (List[BookDataType], ISBNs)

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

  val successfulISBNMatchGenerator = for {
    existingBookData <- Gen.listOfN(25, dataGenerator)
    dataToQuery <- Gen.oneOf(existingBookData)
  } yield (existingBookData, dataToQuery._3)

  val unsuccessfulISBNMatchGenerator = for {
    existingBookData <- Gen.listOfN(26, dataGenerator)
  } yield (existingBookData.drop(1), existingBookData.head._3)

  // Populate a given catalog
  private def populateCatalog(
    initialCatalog : BookCatalog,
    availableBooks: List[BookDataType]
  ) : BookCatalog =
    availableBooks.foldLeft(
      initialCatalog
    ) {
      (catalogBeingPopulated, currentBookData) =>
      currentBookData match {
        case (title, author, isbn, description, coverImage, categories) =>
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

  property("indicates when a book in the catalog has a given ISBN") {
    forAll(catalogGenerator, successfulISBNMatchGenerator) {
      (catalog: BookCatalog, queryData: ISBNQueryDataType) =>
      queryData match {
        case (availableBooks, matchingISBN) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              availableBooks
            )
          exists(
            populatedCatalog,
            matchingISBN
          ) should be (true)
      }
    }
  }

  property("indicates when no book in the catalog has a given ISBN") {
    forAll (catalogGenerator, unsuccessfulISBNMatchGenerator) {
      (catalog: BookCatalog, queryData: ISBNQueryDataType) =>
      queryData match {
        case (availableBooks, differentISBN) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              availableBooks
            )
          exists(
            populatedCatalog,
            differentISBN
          ) should be (false)
      }
    }
  }
}
