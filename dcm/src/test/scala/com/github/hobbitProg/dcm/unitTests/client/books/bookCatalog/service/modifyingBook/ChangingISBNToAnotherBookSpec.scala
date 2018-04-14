package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.modifyingBook

import scala.util.Success

import cats.data.Validated
import Validated._
import cats.scalatest.ValidatedMatchers

import org.scalatest.{PropSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import org.scalacheck.{Gen, Arbitrary}
import Arbitrary.arbitrary
import Gen.const

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.repository.FakeRepository

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.BookCatalogError
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.
  BookCatalogServiceInterpreter
import BookCatalogServiceInterpreter._

/**
  * Specification for setting the ISBN of a book to the ISBN of another book
  * @author Kyle Cranmer
  * @since 0.2
  */
class ChangingISBNToAnotherBookSpec
    extends PropSpec
    with Matchers
    with GeneratorDrivenPropertyChecks
    with ModifyingBookSpec
    with ValidatedMatchers {

  private def modifyISBN(
    populatedCatalog: BookCatalog,
    repository: FakeRepository,
    firstBook: BookInfoType,
    secondBook: BookInfoType
  ) = {
    repository.bookPlacedIntoRepository = null
    repository.bookRemovedFromRepository = null
    firstBook match {
      case (
        title,
        author,
        originalISBN,
        description,
        coverImage,
        categories
      ) =>
        secondBook match {
          case (
            _,
            _,
            newISBN,
            _,
            _,
            _
          ) =>
            val Success(originalBook) =
              getByISBN(
                populatedCatalog,
                originalISBN
              )
            givenOriginalBook = null
            givenUpdatedBook = null
            val catalogWithSubscriber =
              onModify(
                populatedCatalog,
                (originalBook, updatedBook) => {
                  givenOriginalBook = originalBook
                  givenUpdatedBook = updatedBook
                }
              )
            modifyBook(
              catalogWithSubscriber,
              originalBook,
              title,
              author,
              newISBN,
              description,
              coverImage,
              categories
            )(
              repository
            )
        }
    }
  }

  property("indicates the catalog was not updated") {
    forAll(
      catalogGenerator,
      repositoryGenerator,
      twoBookDataGen
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: TwoBookDataType
      ) =>
      bookData match {
        case (firstBookData, secondBookData) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              repository,
              firstBookData,
              secondBookData
            )
          val resultingCatalog =
            modifyISBN(
              populatedCatalog,
              repository,
              firstBookData,
              secondBookData
            )
          resultingCatalog should be (invalid[BookCatalogError])
      }
    }
  }

  property("does not update the repository") {
    forAll(
      catalogGenerator,
      repositoryGenerator,
      twoBookDataGen
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: TwoBookDataType
      ) =>
      bookData match {
        case (firstBookData, secondBookData) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              repository,
              firstBookData,
              secondBookData
            )
          modifyISBN(
            populatedCatalog,
            repository,
            firstBookData,
            secondBookData
          )
          repository.bookPlacedIntoRepository should be (null)
          repository.bookRemovedFromRepository should be (null)
      }
    }
  }

  property("does not give the updated book to the listener") {
    forAll(
      catalogGenerator,
      repositoryGenerator,
      twoBookDataGen
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: TwoBookDataType
      ) =>
      bookData match {
        case (firstBookData, secondBookData) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              repository,
              firstBookData,
              secondBookData
            )
          modifyISBN(
            populatedCatalog,
            repository,
            firstBookData,
            secondBookData
          )
          givenOriginalBook should be (null)
      }
    }
  }

  property("does not give the original book to the listener") {
    forAll(
      catalogGenerator,
      repositoryGenerator,
      twoBookDataGen
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: TwoBookDataType
      ) =>
      bookData match {
        case (firstBookData, secondBookData) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              repository,
              firstBookData,
              secondBookData
            )
          modifyISBN(
            populatedCatalog,
            repository,
            firstBookData,
            secondBookData
          )
          givenUpdatedBook should be (null)
      }
    }
  }
}
