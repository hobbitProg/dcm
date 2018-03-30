package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.modifyingBook

import scala.util.{Try, Success}

import org.scalatest.{PropSpec, Matchers, TryValues}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.matchers.bookCatalog.scalatest._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Specification for removing an author that exists in the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class RemovingAuthorOfBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with TryValues
    with CatalogMatchers
    with AuthorModificationSpec {
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

      modifyAuthorOfBook(
        catalogData,
        ""
      ) should be a 'failure
    }
  }

  property("no book is given to the listener"){
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
      modifyAuthorOfBook(
        catalogData,
        ""
      )
      givenOriginalBook should equal (null)
      givenUpdatedBook should equal (null)
    }
  }
}
