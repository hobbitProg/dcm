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
  * Specification for modifying a description of a book that exists in the
  * catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingDescriptionOfBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with CatalogMatchers
    with BookModificationSpec {

  // Modify the description of a book in the catalog
  private def modifyDescriptionOfBook(
    catalogData: Try[CatalogInfoType],
    newDescription: Description
  ) : Try[BookCatalog] = {
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
    ) =
      catalogData
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
      newDescription,
      coverImage,
      categories
    )
  }

  property("the book with the new description is played into the catalog") {
    forAll(catalogGenerator, DescriptionGen) {
      (catalogData: Try[CatalogInfoType], newDescription: Description) =>
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
      ) =
        catalogData
      modifyDescriptionOfBook(
        catalogData,
        newDescription
      ) should containBook(
        TestBook(
          title,
          author,
          isbn,
          newDescription,
          coverImage,
          categories
        )
      )
    }
  }

  property("the original book is given to all listeners") {
    forAll(catalogGenerator, DescriptionGen) {
      (catalogData: Try[CatalogInfoType], newDescription: Description) =>
      modifyDescriptionOfBook(
        catalogData,
        newDescription
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
    forAll(catalogGenerator, DescriptionGen) {
      (catalogData: Try[CatalogInfoType], newDescription: Description) =>
      modifyDescriptionOfBook(
        catalogData,
        newDescription
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
      val modifiedBook =
        TestBook(
          title,
          author,
          isbn,
          newDescription,
          coverImage,
          categories
        )
      givenUpdatedBook should equal (modifiedBook)
    }
  }
}
