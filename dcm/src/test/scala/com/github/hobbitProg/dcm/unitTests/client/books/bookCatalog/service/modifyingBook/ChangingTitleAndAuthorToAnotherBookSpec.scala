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
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.BookCatalogError
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.
  BookCatalogServiceInterpreter
import BookCatalogServiceInterpreter._

/**
  * Specification for changing the title and author of a book to a different
  * book within the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ChangingTitleAndAuthorToAuthorBookSpec
    extends PropSpec
    with Matchers
    with GeneratorDrivenPropertyChecks
    with ModifyingBookSpec
    with ValidatedMatchers {

  private def modifyTitleAndAuthor(
    populatedCatalog: BookCatalog,
    repository: FakeRepository,
    firstBook: BookInfoType,
    secondBook: BookInfoType
  ) : Validated[BookCatalogError, (BookCatalog, BookCatalogRepository)] = {
    repository.bookPlacedIntoRepository = null
    repository.bookRemovedFromRepository = null
    firstBook match {
      case (
        _,
        _,
        isbn,
        description,
        coverImage,
        categories
      ) =>
        secondBook match {
          case (
            title,
            author,
            _,
            _,
            _,
            _
          ) =>
            val Success(originalBook) =
              getByISBN(
                populatedCatalog,
                isbn
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
              isbn,
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
            modifyTitleAndAuthor(
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
          modifyTitleAndAuthor(
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
          modifyTitleAndAuthor(
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
          modifyTitleAndAuthor(
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
