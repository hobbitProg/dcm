package com.github.hobbitProg.dcm.unitTests.client.bookCatalog.model.book

import cats.data.Validated
import Validated.{Valid, Invalid}

import scala.collection.Set

import org.scalacheck.{Arbitrary, Gen, Prop}
import Arbitrary.arbitrary

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * Specification for creating books
  * @author Kyle Cranmer
  * @since 0.2
  */
class BookCreationSpec
    extends Specification
    with ScalaCheck{

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

  private val validDataGenerator =
    for {
      title <- arbitrary[String].suchThat(_.length > 0)
      author <- arbitrary[String].suchThat(_.length > 0)
      isbn <- arbitrary[String].suchThat(_.length > 0)
      description <- Gen.option(arbitrary[String])
      cover <- Gen.oneOf(availableCovers)
      categories <- Gen.listOf(arbitrary[String])
    } yield ((title, author, isbn, description, cover, categories.toSet))

  "When given valid book information is given to create a book" >> {
    "a book is created" >> {
      Prop.forAllNoShrink(validDataGenerator) {
        (bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, cover, categories) =>
            val newBook =
              Book.book(
                title,
                author,
                isbn,
                description,
                cover,
                categories
              )
              newBook.isInstanceOf[Valid[Book]]
          }
        }
      }
    }

    "the created book has the given information" >> {
      Prop.forAllNoShrink(validDataGenerator) {
        (bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, cover, categories) =>
            val newBook =
              Book.book(
                title,
                author,
                isbn,
                description,
                cover,
                categories
              )
              titleMatches(
                newBook,
                title
              ) &&
              authorMatches(
                newBook,
                author
              ) &&
              isbnMatches(
                newBook,
                isbn
              ) &&
              descriptionMatches(
                newBook,
                description
              ) &&
              coverMatches(
                newBook,
                cover
              ) &&
              categoriesMatch(
                newBook,
                categories
              )
          }
        }
      }
    }
  }

  def titleMatches(
    newBook: Validated[String, Book],
    expectedTitle: Titles
  ): Boolean = {
    newBook match {
      case Valid(book) =>
        book.title == expectedTitle
      case Invalid(_) =>
        false
    }
  }

  def authorMatches(
    newBook: Validated[String, Book],
    expectedAuthor: Authors
  ): Boolean = {
    newBook match {
      case Valid(book) =>
        book.author == expectedAuthor
      case Invalid(_) =>
        false
    }
  }

  def isbnMatches(
    newBook: Validated[String, Book],
    expectedISBN: ISBNs
  ): Boolean = {
    newBook match {
      case Valid(book) =>
        book.isbn == expectedISBN
      case Invalid(_) =>
        false
    }
  }

  def descriptionMatches(
    newBook: Validated[String, Book],
    expectedDescription: Description
  ): Boolean = {
    newBook match {
      case Valid(book) =>
        book.description == expectedDescription
      case Invalid(_) =>
        false
    }
  }

  def coverMatches(
    newBook: Validated[String, Book],
    expectedCover: CoverImages
  ) : Boolean = {
    newBook match {
      case Valid(book) =>
        book.coverImage == expectedCover
      case Invalid(_) =>
        false
    }
  }

  def categoriesMatch(
    newBook: Validated[String, Book],
    expectedCategories: Set[Categories]
  ): Boolean = {
    newBook match {
      case Valid(book) =>
        book.categories == expectedCategories
      case Invalid(_) =>
        false
    }
  }
}
