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
  * Specification for modifying an author of a book that exists in the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingAuthorOfBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with CatalogMatchers
    with AuthorModificationSpec {

  private val authorGenerator = for (
    author <- arbitrary[String].suchThat(_.length > 0)
  ) yield author

  property("the book with the new author is placed in the catalog") {
    forAll(catalogGenerator, authorGenerator) {
      (catalogData: Try[CatalogInfoType], newAuthor: Authors) =>
      val Success((catalog, (title, author, isbn, description, coverImage, categories))) =
        catalogData
      val Success(updatedCatalog) =
        modifyAuthorOfBook(
          catalogData,
          newAuthor
        )
      updatedCatalog should containBook(
        TestBook(
          title,
          newAuthor,
          isbn,
          description,
          coverImage,
          categories
        )
      )
    }
  }

  property("the original book is given to all modification listeners") {
    forAll(catalogGenerator, authorGenerator) {
      (catalogData: Try[CatalogInfoType], newAuthor: Authors) => {
        modifyAuthorOfBook(
          catalogData,
          newAuthor
        )
        val Success((catalog, (title, author, isbn, description, coverImage, categories))) =
          catalogData
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
  }

  property("the modified book is given to all modification listeners") {
    forAll(catalogGenerator, authorGenerator) {
      (catalogData: Try[CatalogInfoType], newAuthor: Authors) => {
        modifyAuthorOfBook(
          catalogData,
          newAuthor
        )
        val Success((catalog, (title, author, isbn, description, coverImage, categories))) =
          catalogData
        val expectedBook =
          TestBook(
            title,
            newAuthor,
            isbn,
            description,
            coverImage,
            categories
          )
        givenUpdatedBook should equal (expectedBook)
      }
    }
  }
}
