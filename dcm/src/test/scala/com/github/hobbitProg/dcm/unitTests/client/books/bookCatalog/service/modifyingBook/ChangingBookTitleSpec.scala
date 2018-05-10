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
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogError
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.
  BookCatalogServiceInterpreter
import BookCatalogServiceInterpreter._

/**
  * Specification for changing the title of the book in the catalog using the
  * service
  * @author Kyle Cranmer
  * @since 0.2
  */
class ChangingBookTitleSpec
    extends PropSpec
    with Matchers
    with GeneratorDrivenPropertyChecks
    with ModifyingBookSpec
    with ValidatedMatchers {

  private type TitleModificationType =
    (
      BookInfoType,
      Titles
    )

  private val titleModificationGenerator = for {
    title <- TitleGen
    author <- AuthorGen
    isbn <- ISBNGen
    description <- DescriptionGen
    coverImage <- CoverImageGen
    categories <- CategoriesGen
    newTitle <- TitleGen.suchThat(
      generatedTitle =>
      generatedTitle != title
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
    newTitle
  )

  private def modifyTitle(
    populatedCatalog: BookCatalog,
    repository: FakeRepository,
    bookData: TitleModificationType
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
        newTitle
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
          newTitle,
          author,
          isbn,
          description,
          coverImage,
          categories
        )(
          repository
        )
    }

  property("indicates the catalog was updated") {
    forAll(
      catalogGenerator,
      repositoryGenerator,
      titleModificationGenerator
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: TitleModificationType
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
            modifyTitle(
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
      titleModificationGenerator
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: TitleModificationType
      ) =>
      bookData match {
        case (originalData, newTitle) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              repository,
              originalData
            )
          val Valid((resultingCatalog, _)) =
            modifyTitle(
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
                  newTitle,
                  author,
                  isbn,
                  description,
                  coverImage,
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
      titleModificationGenerator
    ) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: TitleModificationType
      ) =>
      bookData match {
        case (originalData, newTitle) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              repository,
              originalData
            )
          val resultingCatalog =
            modifyTitle(
              populatedCatalog,
              repository,
              bookData
            )
          originalData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val expectedBook =
                TestBook(
                  newTitle,
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
              updatedBook should be (expectedBook)
          }
      }
    }
  }

  property("gives the updated book to the listener") {
    forAll(catalogGenerator, repositoryGenerator, titleModificationGenerator) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: TitleModificationType
      ) =>
      bookData match {
        case (originalData, newTitle) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              repository,
              originalData
            )
          val resultingCatalog =
            modifyTitle(
              populatedCatalog,
              repository,
              bookData
            )
          originalData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val expectedBook =
                TestBook(
                  newTitle,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              givenUpdatedBook should be (expectedBook)
          }
      }
    }
  }

  property("removes the original book from the catalog") {
    forAll(catalogGenerator, repositoryGenerator, titleModificationGenerator) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: TitleModificationType
      ) =>
      bookData match {
        case (originalData, newTitle) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              repository,
              originalData
            )
          val Valid((resultingCatalog, _)) =
            modifyTitle(
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
              val Success(updatedBook) =
                getByISBN(
                  resultingCatalog,
                  isbn
                )
              updatedBook should not be (originalBook)
          }
      }
    }
  }

  property("removes the original book from the repository") {
    forAll(catalogGenerator, repositoryGenerator, titleModificationGenerator) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: TitleModificationType
      ) =>
      bookData match {
        case (originalData, newTitle) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              repository,
              originalData
            )
          val resultingCatalog =
            modifyTitle(
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
              repository.bookRemovedFromRepository should be (originalBook)
          }
      }
    }
  }

  property("gives the original book to the listener") {
    forAll(catalogGenerator, repositoryGenerator, titleModificationGenerator) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: TitleModificationType
      ) =>
      bookData match {
        case (originalData, newTitle) =>
          val populatedCatalog =
            populateCatalog(
              catalog,
              repository,
              originalData
            )
          val resultingCatalog =
            modifyTitle(
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
