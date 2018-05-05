package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.addingBook

import cats.data.Validated.Valid
import cats.scalatest.ValidatedMatchers

import org.scalacheck.{Gen, Arbitrary, Prop}
import Arbitrary.arbitrary
import Gen.const

import org.scalatest.{PropSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.matchers.bookCatalog.scalatest.
  {CatalogMatchers, RepositoryMatchers}

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.repository.FakeRepository

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.
  BookCatalogServiceInterpreter
import BookCatalogServiceInterpreter._

/**
  * Specification for adding a new book with valid information to the catalog
  * using the service
  * @author Kyle Cranmer
  * @since 0.2
  */
class AllValidInformationSpec
    extends PropSpec
    with Matchers
    with GeneratorDrivenPropertyChecks
    with AddingBookSpec
    with ValidatedMatchers
    with RepositoryMatchers
    with CatalogMatchers {

  val dataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield (title, author, isbn, description, coverImage, categories.toSet)

  property("indicates the book was added to the catalog") {
    forAll(catalogGenerator, repositoryGenerator, dataGenerator) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: BookDataType
      ) =>
      bookData match {
        case (title, author, isbn, description, coverImage, categories) =>
          val updatedCatalog =
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
          updatedCatalog should be (valid)
      }
    }
  }

  property("places the book into the catalog") {
    forAll(catalogGenerator, repositoryGenerator, dataGenerator) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: BookDataType
      ) =>
      bookData match {
        case (title, author, isbn, description, coverImage, categories) =>
          val Valid((resultingCatalog, _)) =
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
          resultingCatalog should haveBook(
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

  property("places the book into the repository") {
    forAll (catalogGenerator, repositoryGenerator, dataGenerator) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: BookDataType
      ) =>
        bookData match {
          case (title, author, isbn, description, coverImage, categories) =>
          val Valid((_, resultingRepository)) =
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
            resultingRepository should includeBook(
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
