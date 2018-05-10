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
  * Specification for trying to remove the title of a book using the service
  * @author Kyle Cranmer
  * @since 0.2
  */
class RemovingBookTitleSpec
    extends PropSpec
    with Matchers
    with GeneratorDrivenPropertyChecks
    with ModifyingBookSpec
    with ValidatedMatchers {

  private def removeTitle(
    populatedCatalog: BookCatalog,
    repository: FakeRepository,
    bookData: BookInfoType
  ) : Validated[BookCatalogError, (BookCatalog, BookCatalogRepository)] =
    bookData match {
      case (
        _,
        author,
        isbn,
        description,
        coverImage,
        categories
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
          "",
          author,
          isbn,
          description,
          coverImage,
          categories
        )(
          repository
        )
    }

  property("indicates the catalog was not updated") {
    forAll(
      catalogGenerator,
      repositoryGenerator,
      bookDataGen
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: BookInfoType
      ) =>
      val populatedCatalog =
        populateCatalog(
          catalog,
          repository,
          bookData
        )
      val resultingCatalog =
        removeTitle(
          populatedCatalog,
          repository,
          bookData
        )
      resultingCatalog should be (invalid[BookCatalogError])
    }
  }

  property("does not update the repository") {
    forAll(
      catalogGenerator,
      repositoryGenerator,
      bookDataGen
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: BookInfoType
      ) =>
      val populatedCatalog =
        populateCatalog(
          catalog,
          repository,
          bookData
        )
      removeTitle(
        populatedCatalog,
        repository,
        bookData
      )
      bookData match {
        case (title, author, isbn, description, coverImage, categories) =>
          val originalBook =
            TestBook(
              title,
              author,
              isbn,
              description,
              coverImage,
              categories
            )
          val Right(updatedBook) =
            repository.retrieve(
              isbn
            )
          updatedBook should be (originalBook)
      }
    }
  }

  property("does not give the updated book to the listener") {
    forAll(
      catalogGenerator,
      repositoryGenerator,
      bookDataGen
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: BookInfoType
      ) =>
      val populatedCatalog =
        populateCatalog(
          catalog,
          repository,
          bookData
        )
      removeTitle(
        populatedCatalog,
        repository,
        bookData
      )
      givenUpdatedBook should be (null)
    }
  }

  property("does not give the original book to the listener") {
    forAll(
      catalogGenerator,
      repositoryGenerator,
      bookDataGen
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: BookInfoType
      ) =>
      val populatedCatalog =
        populateCatalog(
          catalog,
          repository,
          bookData
        )
      removeTitle(
        populatedCatalog,
        repository,
        bookData
      )
      givenOriginalBook should be (null)
    }
  }
}
