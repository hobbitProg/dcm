package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.modifyingBook

import scala.collection.Set
import scala.util.{Try, Success, Failure}

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop._

import org.scalacheck.{Gen, Arbitrary}
import Arbitrary.arbitrary
import Gen.const

import com.github.hobbitProg.dcm.matchers.bookCatalog.scalatest._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Specification for modifying an ISBN of a book that exists in the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingISBNOfBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with CatalogMatchers
    with ISBNModificationSpec {

  property("the book with the new ISBN is placed into the catalog") {
    forAll(catalogGenerator, ISBNGen) {
      (catalogData: Try[CatalogInfoType], newISBN: ISBNs) =>
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
        newISBN
      ) should containBook(
        TestBook(
          title,
          author,
          newISBN,
          description,
          coverImage,
          categories
        )
      )
    }
  }

  property("the original book is given to all listeners") {
    forAll(catalogGenerator, ISBNGen) {
      (catalogData: Try[CatalogInfoType], newISBN: ISBNs) =>
      modifyISBNOfBook(
        catalogData,
        newISBN
      )
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
      val originalBook =
        TestBook(
          title,
          author,
          isbn,
          description,
          coverImage,
          categories
        )
      givenOriginalBook should equal (originalBook)
    }
  }

  property("the updated book is given to all listeners") {
    forAll(catalogGenerator, ISBNGen) {
      (catalogData: Try[CatalogInfoType], newISBN: ISBNs) =>
      modifyISBNOfBook(
        catalogData,
        newISBN
      )
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
      val updatedBook =
        TestBook(
          title,
          author,
          newISBN,
          description,
          coverImage,
          categories
        )
      givenUpdatedBook should equal (updatedBook)
    }
  }
}
