package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.queryingBook

import cats.data.Validated.Valid

import org.scalatest.{PropSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.repository.FakeRepository

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.
  BookCatalogServiceInterpreter
import BookCatalogServiceInterpreter._

/**
  * Specification for querying the book catalog with a title and autor using the
  * service
  * @author Kyle Cranmer
  * @since 0.2
  */
class QueryingUsingTitleAndAuthorSpec
    extends PropSpec
    with Matchers
    with GeneratorDrivenPropertyChecks
    with QuerySpec {
  property("indicates a book exists when a book in the catalog has the given " +
    "title and author") {
    forAll(catalogGenerator, repositoryGenerator, dataGenerator) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: BookDataType
      ) =>
      bookData match {
        case (title, author, isbn, description, coverImage, categories) =>
          val Valid(resultingCatalog) =
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
          bookExists(
            resultingCatalog,
            title,
            author
          )(
            repository
          ) should be (true)
      }
    }
  }

  property("indicates a book exists when a book in the repository has the " +
    "given title and author") {
    forAll(catalogGenerator, repositoryGenerator, dataGenerator) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: BookDataType
      ) =>
      bookData match {
        case (title, author, isbn, description, coverImage, categories) =>
          repository.existingTitle = title
          repository.existingAuthor = author
          bookExists(
            catalog,
            title,
            author
          )(
            repository
          ) should be (true)
      }
    }
  }

  property("indicates no book exists when no book exists in the catalog nor " +
    "the repository has the given title and author") {
    forAll(catalogGenerator, repositoryGenerator, dataGenerator) {
      (
        catalog: BookCatalog,
        repository: FakeRepository,
        bookData: BookDataType
      ) =>
      bookData match {
        case (title, author, isbn, description, coverImage, categories) =>
          bookExists(
            catalog,
            title,
            author
          )(
            repository
          ) should be (false)
      }
    }
  }
}
