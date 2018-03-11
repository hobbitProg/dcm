package com.github.hobbitProg.dcm.unitTests.client.bookCatalog.model.book

import cats.data.Validated.Invalid

import scala.collection.Set

import org.scalatest.{PropSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import org.scalacheck.{Arbitrary, Gen, Prop}
import Arbitrary.arbitrary

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * Specification for trying to create a book with no title
  * @author Kyle Cranmer
  * @since 0.2
  */
class BookWithNoTitleSpec
    extends PropSpec
    with Matchers
    with GeneratorDrivenPropertyChecks
    with BookSpecification {

  private val noTitleDataGenerator =
    for {
      author <- arbitrary[String].suchThat(_.length > 0)
      isbn <- arbitrary[String].suchThat(_.length > 0)
      description <- Gen.option(arbitrary[String])
      cover <- Gen.oneOf(availableCovers)
      categories <- Gen.listOf(arbitrary[String])
    } yield (("", author, isbn, description, cover, categories.toSet))

  property("no book is created") {
    forAll (noTitleDataGenerator) {
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
          ) shouldBe an [Invalid[_]]
      }
    }
  }
}
