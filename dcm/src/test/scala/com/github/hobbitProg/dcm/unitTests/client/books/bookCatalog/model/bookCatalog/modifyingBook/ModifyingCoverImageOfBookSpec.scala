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
  * Specification for modifying the cover of a book that exists in the
  * catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingCoverImageOfBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with CatalogMatchers
    with BookModificationSpec {

  // Modify the cover of a book in the catalog
  private def modifyCoverOfBook(
    catalogData: Try[CatalogInfoType],
    newCover: CoverImages
  ) = {
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
    val catalogWithSubscriber =
      onModify(
        catalog,
        (originalBook, updatedBook) => {
          givenOriginalBook = originalBook
          givenUpdatedBook = updatedBook
        }
      )
    val Success(originalBook) =
      getByISBN(
        catalogWithSubscriber,
        isbn
      )
    updateBook(
      catalogWithSubscriber,
      originalBook,
      title,
      author,
      isbn,
      description,
      newCover,
      categories
    )
  }

  property("the book with the new cover is placed into the catalog") {
    forAll(catalogGenerator, CoverImageGen) {
      (catalogData: Try[CatalogInfoType], newCover: CoverImages) =>
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
      modifyCoverOfBook(
        catalogData,
        newCover
      ) should containBook(
        TestBook(
          title,
          author,
          isbn,
          description,
          newCover,
          categories
        )
      )
    }
  }

  property("the original book is given to all listeners") {
    forAll(catalogGenerator, CoverImageGen) {
      (catalogData: Try[CatalogInfoType], newCover: CoverImages) =>
      modifyCoverOfBook(
        catalogData,
        newCover
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

  property("the modified book is given to all listeners") {
    forAll(catalogGenerator, CoverImageGen) {
      (catalogData: Try[CatalogInfoType], newCover: CoverImages) =>
      modifyCoverOfBook(
        catalogData,
        newCover
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
      val expectedBook =
        TestBook(
          title,
          author,
          isbn,
          description,
          newCover,
          categories
        )
      givenUpdatedBook should equal (expectedBook)
    }
  }
}
