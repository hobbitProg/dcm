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
  * Specification for trying to delete a book that does not exist in the catalog
  * @author Kyle Cranmer
  * @since 0.3
  */
class DeletingNonExistentBookSpec
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
    Authors
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
    differentTitle <- TitleGen.suchThat(
      generatedTitle =>
      generatedTitle != title
    )
    author <- AuthorGen
    differentAuthor <- AuthorGen.suchThat(
      generatedAuthor =>
      generatedAuthor != author
    )
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
    differentTitle,
    differentAuthor
  )

  property("indicates the catalog was not updated") {
    forAll(
      dataGenerator
    ) {
      (
        testData: Try[DeleteInfoType]
      ) =>
      val Success((catalog, repository, title, author)) =
        testData
      delete(
        catalog,
        title,
        author
      )(
        repository
      ) should be (invalid)
    }
  }

  property("does not modify the repository") {
    forAll(
      dataGenerator
    ) {
      (
        testData: Try[DeleteInfoType]
      ) =>
      val Success((catalog, repository, title, author)) =
        testData
      repository.asInstanceOf[FakeRepository].deletedISBN = null
      delete(
        catalog,
        title,
        author
      )(
        repository
      )
      repository.asInstanceOf[FakeRepository].deletedISBN should be (null)
    }
  }

  property("does not give the book to the listener") {
    forAll(
      dataGenerator
    ) {
      (
        testData: Try[DeleteInfoType]
      ) =>
      val Success((catalog, repository, title, author)) =
        testData
      var deletedBook : Book = null
      val updatedCatalog =
        onDelete(
          catalog,
          book =>
          deletedBook = book
        )
      delete(
        updatedCatalog,
        title,
        author
      )(
        repository
      )
      deletedBook should be (null)
    }
  }
}
