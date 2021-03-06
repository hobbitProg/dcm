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
  * Specification for changing the cover of a book in the catalog using the
  * service
  * @author Kyle Cranmer
  * @since 0.2
  */
class ChangingBookCoverSpec
    extends PropSpec
    with Matchers
    with GeneratorDrivenPropertyChecks
    with ModifyingBookSpec
    with ValidatedMatchers {

  private type CoverModificationType =
    (
      BookInfoType,
      CoverImages
    )

  private val coverModificationGenerator = for {
    title <- TitleGen
    author <- AuthorGen
    isbn <- ISBNGen
    description <- DescriptionGen
    coverImage <- CoverImageGen
    categories <- CategoriesGen
    newCover <- CoverImageGen.suchThat(
      generatedCover =>
      generatedCover != coverImage
    )
  } yield (
    (
      title,
      author,
      isbn,
      description,
      coverImage,
      categories
    ),
    newCover
  )

  private def modifyCover(
    populatedCatalog: BookCatalog,
    repository: FakeRepository,
    bookData: CoverModificationType
  ) : Validated[BookCatalogError, (BookCatalog, BookCatalogRepository)] =
    bookData match {
      case (
        (
          title,
          author,
          isbn,
          description,
          coverImage,
          categories
        ),
        newCover
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
          newCover,
          categories
        )(
          repository
        )
    }

  property("indicates the catalog was updated") {
    forAll(
      catalogGenerator,
      repositoryGenerator,
      coverModificationGenerator
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: CoverModificationType
      ) =>
      bookData match {
        case (originalData, _) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              repository,
              originalData
            )
          val resultingCatalog =
            modifyCover(
              populatedCatalog,
              repository,
              bookData
            )
          resultingCatalog should be (valid)
      }
    }
  }
  property("places the updated book in the catalog") {
    forAll(
      catalogGenerator,
      repositoryGenerator,
      coverModificationGenerator
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: CoverModificationType
      ) =>
      bookData match {
        case (originalData, newCover) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              repository,
              originalData
            )
          val Valid((resultingCatalog, _)) =
            modifyCover(
              populatedCatalog,
              repository,
              bookData
            )
          originalData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val Success(updatedBook) =
                getByISBN(
                  resultingCatalog,
                  isbn
                )
              val expectedBook =
                TestBook(
                  title,
                  author,
                  isbn,
                  description,
                  newCover,
                  categories
                )
              updatedBook should be (expectedBook)
          }
      }
    }
  }

  property("places the updated book in the repository") {
    forAll(
      catalogGenerator,
      repositoryGenerator,
      coverModificationGenerator
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: CoverModificationType
      ) =>
      bookData match {
        case (originalData, newCover) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              repository,
              originalData
            )
          val resultingCatalog =
            modifyCover(
              populatedCatalog,
              repository,
              bookData
            )
          originalData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val expectedBook =
                TestBook(
                  title,
                  author,
                  isbn,
                  description,
                  newCover,
                  categories
                )
              val Right(updatedBook) =
                repository.retrieve(
                  isbn
                )
              updatedBook should be (expectedBook)
          }
      }
    }
  }

  property("gives the updated book to the listener") {
    forAll(
      catalogGenerator,
      repositoryGenerator,
      coverModificationGenerator
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: CoverModificationType
      ) =>
      bookData match {
        case (originalData, newCover) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              repository,
              originalData
            )
          val resultingCatalog =
            modifyCover(
              populatedCatalog,
              repository,
              bookData
            )
          originalData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val expectedBook =
                TestBook(
                  title,
                  author,
                  isbn,
                  description,
                  newCover,
                  categories
                )
              givenUpdatedBook should be (expectedBook)
          }
      }
    }
  }

  property("gives the original book to the listener") {
    forAll(
      catalogGenerator,
      repositoryGenerator,
      coverModificationGenerator
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: CoverModificationType
      ) =>
      bookData match {
        case (originalData, newCover) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              repository,
              originalData
            )
          val resultingCatalog =
            modifyCover(
              populatedCatalog,
              repository,
              bookData
            )
          originalData match {
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
              givenOriginalBook should be (originalBook)
          }
      }
    }
  }
}
