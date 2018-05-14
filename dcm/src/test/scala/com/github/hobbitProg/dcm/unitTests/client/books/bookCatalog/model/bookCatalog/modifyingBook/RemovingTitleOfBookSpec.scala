package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.modifyingBook

import scala.collection.Set
import scala.util.{Try, Success, Failure}

import org.scalatest.{Matchers, PropSpec, TryValues}
import org.scalatest.prop._

import com.github.hobbitProg.dcm.matchers.bookCatalog.scalatest._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Specification for removing a title of a book that exists in the
  * catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class RemovingTitleOfBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with CatalogMatchers
    with TryValues
    with TitleModificationSpec {

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
      modifyTitleOfBook(
        catalogData,
        ""
      ) should be a 'failure
    }
  }

  property("no book is given to the modification listener"){
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
      modifyTitleOfBook(
        catalogData,
        ""
      )
      givenOriginalBook should equal (null)
      givenUpdatedBook should equal (null)
    }
  }
}
