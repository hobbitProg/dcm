package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model

import scala.List
import scala.util.Success

import org.scalacheck.{Gen, Arbitrary, Prop}
import Arbitrary._
import Gen.const

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

class QueryingSpec
    extends Specification
    with ScalaCheck {
  private type BookDataType = (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories])
  private type QueryDataType = (List[BookDataType], Titles, Authors)
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
    existingBookData <- Gen.listOf(dataGenerator).suchThat(_.length > 0)
    dataToQuery <- Gen.oneOf(existingBookData)
  } yield (existingBookData, dataToQuery._1, dataToQuery._2)

  val unsuccessfulTitleAuthorMatchGenerator = for {
    existingBookData <- Gen.listOf(dataGenerator).suchThat(_.length > 0)
    dataToQuery <- dataGenerator.suchThat(!existingBookData.toSet.contains(_))
  } yield (existingBookData, dataToQuery._1, dataToQuery._2)

  "Searching for a book using the book's title and author" >> {
    "indicates when a book in the catalog has the given title and author" >> {
      Prop.forAllNoShrink(catalogGenerator, successfulTitleAuthorMatchGenerator) {
        (catalog: BookCatalog, queryData: QueryDataType) => {
          queryData match {
            case (availableBooks, matchingTitle, matchingAuthor) =>
              val populatedCatalog =
                availableBooks.foldLeft(
                  catalog
                ){
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
              exists(
                populatedCatalog,
                matchingTitle,
                matchingAuthor
              )
          }
        }
      }
    }

    "indicates when no book in the catalog has the given title and author" >> {
      Prop.forAllNoShrink(catalogGenerator, unsuccessfulTitleAuthorMatchGenerator) {
        (catalog: BookCatalog, queryData: QueryDataType) => {
          queryData match {
            case (availableBooks, differentTitle, differentAuthor) =>
              val populatedCatalog =
                availableBooks.foldLeft(
                  catalog
                ){
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
              !exists(
                populatedCatalog,
                differentTitle,
                differentAuthor
              )
          }
        }
      }
    }
  }

  "Searching for a book using the book's ISBN" >> {
    "indicates when a book in the catalog has the given ISBN" >> pending
    "indicates when no book in the catalog has the given ISBN" >> pending
  }
}
