package com.github.hobbitProg.dcm.unitTests.client.bookCatalog.model.book

import cats.data.Validated.Valid

import scala.collection.Set

import org.scalatest.{PropSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import org.scalacheck.{Arbitrary, Gen, Prop}
import Arbitrary.arbitrary

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * Specification for creating a book with all valid information
  * @author Kyle Cranmer
  * @since 0.2
  */
class ValidBookDataSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with BookSpecification {

  private val validDataGenerator =
    for {
      title <- arbitrary[String].suchThat(_.length > 0)
      author <- arbitrary[String].suchThat(_.length > 0)
      isbn <- arbitrary[String].suchThat(_.length > 0)
      description <- Gen.option(arbitrary[String])
      cover <- Gen.oneOf(availableCovers)
      categories <- Gen.listOf(arbitrary[String])
    } yield ((title, author, isbn, description, cover, categories.toSet))

  property("a book is created") {
    forAll(validDataGenerator) {
      (bookData: BookDataType) =>
      bookData match {
        case (title, author, isbn, description, cover, categories) =>
          Book.book(
            title,
            author,
            isbn,
            description,
            cover,
            categories
          ) shouldBe a [Valid[_]]
      }
    }
  }

  property("the created book has the given information") {
    forAll(validDataGenerator) {
      (bookData: BookDataType) =>
      bookData match {
        case (title, author, isbn, description, cover, categories) =>
          val Valid(newBook) =
            Book.book(
              title,
              author,
              isbn,
              description,
              cover,
              categories
            )
          newBook.title should equal (title)
          newBook.author should equal (author)
          newBook.isbn should equal (isbn)
          newBook.description should equal (description)
          newBook.coverImage should equal (cover)
          newBook.categories should equal (categories)
      }
    }
  }
}
