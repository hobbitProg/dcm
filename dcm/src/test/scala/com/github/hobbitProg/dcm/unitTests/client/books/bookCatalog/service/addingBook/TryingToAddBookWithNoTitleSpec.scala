package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.addingBook

import cats.data.Validated.Valid
import cats.scalatest.ValidatedMatchers

import org.scalacheck.{Gen, Arbitrary, Prop}
import Arbitrary.arbitrary
import Gen.const

import org.scalatest.{PropSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.matchers.bookCatalog.scalatest.{CatalogMatchers, RepositoryMatchers}

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.repository.FakeRepository

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.BookCatalogError
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.
  BookCatalogServiceInterpreter
import BookCatalogServiceInterpreter._

/**
  * Specification for trying to add a new book with no title to the catalog
  * using the service
  * @author Kyle Cranmer
  * @since 0.2
  */
class TryingToAddBookWithNoTitleSpec
    extends PropSpec
    with Matchers
    with GeneratorDrivenPropertyChecks
    with AddingBookSpec
    with ValidatedMatchers
    with RepositoryMatchers
    with CatalogMatchers {

  val noTitleGenerator = for {
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield ("", author, isbn, description, coverImage, categories.toSet)

  property("indicates the book was not added to the catalog") {
    forAll(catalogGenerator, repositoryGenerator, noTitleGenerator) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: BookDataType
      ) =>
      bookData match {
        case (title, author, isbn, description, coverImage, categories) =>
          val resultingCatalog =
            insertBook(
              catalog,
              title,
              author,
              isbn,
              description,
              coverImage,
              categories
            )(
              repository
            )
          resultingCatalog should be (invalid[BookCatalogError])
      }
    }
  }

  property("does not place the book into the repository") {
    forAll(catalogGenerator, repositoryGenerator, noTitleGenerator) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: BookDataType
      ) =>
      bookData match {
        case (title, author, isbn, description, coverImage, categories) =>
          val resultingCatalog =
            insertBook(
              catalog,
              title,
              author,
              isbn,
              description,
              coverImage,
              categories
            )(
              repository
            )
          repository should notIncludeBook(
            TestBook(
              title,
              author,
              isbn,
              description,
              coverImage,
              categories
            )
          )
      }
    }
  }
}
