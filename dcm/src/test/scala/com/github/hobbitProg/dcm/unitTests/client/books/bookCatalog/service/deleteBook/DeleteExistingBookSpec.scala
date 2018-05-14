package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.deletBook

import scala.util.{Try, Success}

import cats.data.Validated
import Validated._
import cats.scalatest.ValidatedMatchers

import org.scalacheck.{Gen, Arbitrary, Prop}
import Arbitrary.arbitrary
import Gen.const

import org.scalatest.{PropSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.matchers.bookCatalog.scalatest.
  {CatalogMatchers, RepositoryMatchers}

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.
  repository.FakeRepository
import com.github.hobbitProg.dcm.generator.BookGenerators

import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.
  BookCatalogServiceInterpreter
import BookCatalogServiceInterpreter._

/**
  * Specification for deleting an existing book from the catalog using the
  * service
  * @author Kyle Cranmer
  * @since 0.3
  */
class DeleteExistingBookSpec
    extends PropSpec
    with Matchers
    with BookGenerators
    with GeneratorDrivenPropertyChecks
    with ValidatedMatchers
    with RepositoryMatchers
    with CatalogMatchers {

  type DeleteInfoType = (
    BookCatalog,
    BookCatalogRepository,
    Titles,
    Authors,
    ISBNs
  )

  case class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book {
  }

  val dataGenerator = for {
    title <- TitleGen
    author <- AuthorGen
    isbn <- ISBNGen
    description <- DescriptionGen
    coverImage <- CoverImageGen
    categories <- CategoriesGen
    catalog <- addBook(
      new BookCatalog(),
      title,
      author,
      isbn,
      description,
      coverImage,
      categories
    )
    repository <- (new FakeRepository()).add(
      TestBook(
        title,
        author,
        isbn,
        description,
        coverImage,
        categories
      )
    )
  } yield (
    catalog,
    repository,
    title,
    author,
    isbn
  )

  property("indicates the catalog was updated") {
    forAll(
      dataGenerator
    ) {
      (
        testData: Try[DeleteInfoType]
      ) =>
      val Success((catalog, repository, title, author, _)) =
        testData
      delete(
        catalog,
        title,
        author
      )(
        repository
      ) should be (valid)
    }
  }

  property("removes the book from the catalog") {
    forAll(
      dataGenerator
    ) {
      (
        testData: Try[DeleteInfoType]
      ) =>
      val Success((catalog, repository, title, author, _)) =
        testData
      val Valid((updatedCatalog, _)) =
        delete(
          catalog,
          title,
          author
        )(
          repository
        )
      exists(
        updatedCatalog,
        title,
        author
      ) should be (false)
    }
  }

  property("removes the book from the repository") {
    forAll(
      dataGenerator
    ) {
      (
        testData: Try[DeleteInfoType]
      ) =>
      val Success((catalog, repository, title, author, isbn)) =
        testData
      delete(
        catalog,
        title,
        author
      )(
        repository
      )
      repository.asInstanceOf[FakeRepository].deletedISBN should be (isbn)
    }
  }

  property("gives the book to the listener") {
    forAll(
      dataGenerator
    ) {
      (
        testData: Try[DeleteInfoType]
      ) =>
      val Success((catalog, repository, title, author, isbn)) =
        testData
      var deletedBook : Book = null
      val updatedCatalog =
        onDelete(
          catalog,
          book =>
          deletedBook = book
        )
      val Success(
        expectedBook
      ) =
        getByISBN(
          updatedCatalog,
          isbn
        )
      delete(
        updatedCatalog,
        title,
        author
      )(
        repository
      )

      deletedBook should be (expectedBook)
    }
  }
}
