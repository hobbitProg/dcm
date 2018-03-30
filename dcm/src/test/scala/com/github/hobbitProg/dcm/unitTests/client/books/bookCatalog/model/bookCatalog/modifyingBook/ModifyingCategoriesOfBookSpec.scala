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
  * Specification for modifying the categories of  book that exists in
  * the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingCategoriesOfBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with CatalogMatchers
    with BookModificationSpec {

  // Modify the categories associated with a book in the catalog
  private def modifyCategoriesOfBook(
    catalogData: Try[CatalogInfoType],
    newCategories: Set[Categories]
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
      coverImage,
      newCategories
    )
  }

  property("the book with the new categories is placed into the " +
    "catalog") {
    forAll(catalogGenerator, CategoriesGen) {
      (catalogData: Try[CatalogInfoType],
        newCategories: Set[Categories]) =>
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
      modifyCategoriesOfBook(
        catalogData,
        newCategories
      ) should  containBook(
        new TestBook(
          title,
          author,
          isbn,
          description,
          coverImage,
          newCategories
        )
      )
    }
  }

  property("the original book is given to all listeners") {
    forAll(catalogGenerator, CategoriesGen) {
      (catalogData: Try[CatalogInfoType],
        newCategories: Set[Categories]) =>
      modifyCategoriesOfBook(
        catalogData,
        newCategories
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
    forAll(catalogGenerator, CategoriesGen) {
      (catalogData: Try[CatalogInfoType],
        newCategories: Set[Categories]) =>
      modifyCategoriesOfBook(
        catalogData,
        newCategories
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
          coverImage,
          newCategories
        )
      givenUpdatedBook should equal (expectedBook)
    }
  }
}
