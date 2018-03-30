package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.modifyingBook

import scala.util.{Try, Success}

import com.github.hobbitProg.dcm.matchers.bookCatalog.scalatest._

import org.scalatest.{Matchers, PropSpec, TryValues}
import org.scalatest.prop._

/**
  * Specification for removing an ISBN of a book that exists in the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class RemovingISBNOfBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with CatalogMatchers
    with TryValues
    with ISBNModificationSpec {
  property("an indication that an error occurred is generated") {
    forAll (catalogGenerator) {
      (catalogData: Try[CatalogInfoType]) =>
      val Success(
        (
          catalog,
          (
            title,
            author,
            isbn,
            description,
            coverImage,
            categories
          )
        )
      ) = catalogData
      modifyISBNOfBook(
        catalogData,
        ""
      ) should be a 'failure
    }
  }

  property("no book is given to the listener") {
    forAll (catalogGenerator) {
      (catalogData: Try[CatalogInfoType]) =>
      val Success(
        (
          catalog,
          (
            title,
            author,
            isbn,
            description,
            coverImage,
            categories
          )
        )
      ) = catalogData
      modifyISBNOfBook(
        catalogData,
        ""
      )
      givenOriginalBook should equal (null)
      givenUpdatedBook should equal (null)
    }
  }
}
