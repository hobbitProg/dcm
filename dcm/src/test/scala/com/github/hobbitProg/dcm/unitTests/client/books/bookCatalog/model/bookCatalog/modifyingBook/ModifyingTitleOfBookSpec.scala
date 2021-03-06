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
  * Specification for modifying a title of a book that exists in the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingExistingBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with CatalogMatchers
    with TitleModificationSpec {

  property("the book with the new title is placed into the catalog") {
    forAll(catalogGenerator, TitleGen) {
      (catalogData: Try[CatalogInfoType], newTitle: Titles) =>
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
      val Success(updatedCatalog) =
        modifyTitleOfBook(
          catalogData,
          newTitle
        )
      updatedCatalog should containBook(
        TestBook(
          newTitle,
          author,
          isbn,
          description,
          coverImage,
          categories
        )
      )
    }
  }

  property("the original book is given to all modification listeners") {
    forAll(catalogGenerator, TitleGen) {
      (catalogData: Try[CatalogInfoType], newTitle: Titles) =>
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
        newTitle
      )
      val expectedBook =
        TestBook(
          title,
          author,
          isbn,
          description,
          coverImage,
          categories
        )
      givenOriginalBook should equal (expectedBook)
    }
  }

  property("the modified book is given to all modification listeners") {
    forAll(catalogGenerator, TitleGen) {
      (catalogData: Try[CatalogInfoType], newTitle: Titles) =>
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
        newTitle
      )
      val expectedBook =
        TestBook(
          newTitle,
          author,
          isbn,
          description,
          coverImage,
          categories
        )
      givenUpdatedBook should equal (expectedBook)
    }
  }
}
