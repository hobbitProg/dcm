package com.github.dcm.unitTests.client.books.bookCatalog.repository.modifyingBook

import scala.collection.Set

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Prop, Gen}
import Gen.const

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.database.StubDatabase

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Specificatgion for trying to remove the author of a book within the book
  * repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class RemovingBookAuthorSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with ModifyingAuthorSpec {
  val removedAuthorDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield (title, author, isbn, description, coverImage, categories.toSet, "")

  property("the repository is not updated") {
    forAll(databaseGenerator, repositoryGenerator, removedAuthorDataGenerator) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewAuthor
      ) =>
      modifyAuthorOfBook(
        database,
        repository,
        bookData
      ) should be ('left)
    }
  }
  property("the repository was not updated") {
    forAll(databaseGenerator, repositoryGenerator, removedAuthorDataGenerator) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewAuthor
      ) =>
      bookData match {
        case (title, author, isbn, description, coverImage, categories, _) =>
          database.addedTitle = ""
          database.addedAuthor = ""
          database.addedISBN = ""
          database.addedDescription = None
          database.addedCover = None
          database.addedCategoryAssociations = Set[(ISBNs, Categories)]()

          modifyAuthorOfBook(
            database,
            repository,
            bookData
          )
          val savedBook =
            TestBook(
              database.addedTitle,
              database.addedAuthor,
              database.addedISBN,
              database.addedDescription,
              database.addedCover,
              database.addedCategoryAssociations.filter {
                association =>
                association._1 == isbn
              }.map {
                association =>
                association._2
              }
            )
          val expectedBook =
            TestBook(
              "",
              "",
              "",
              None,
              None,
              Set[Categories]()
            )
          savedBook should equal (expectedBook)
      }
    }
  }
}
