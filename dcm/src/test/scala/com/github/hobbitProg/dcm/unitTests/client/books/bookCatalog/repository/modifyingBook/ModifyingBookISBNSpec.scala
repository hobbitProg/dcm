package com.github.dcm.unitTests.client.books.bookCatalog.repository.modifyingBook

import scala.collection.Set
import scala.util.Success

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Prop, Gen}
import Gen.const

import org.scalatest.{Matchers, PropSpec, TryValues}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.database.StubDatabase

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Specification for modifying the ISBN of a book within the book repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingBookISBNSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with TryValues
    with ModifyingISBNSpec {

  val newISBNDataGenerator = for {
    title <- TitleGen
    author <- AuthorGen
    isbn <- ISBNGen
    description <- DescriptionGen
    coverImage <- CoverImageGen
    categories <- CategoriesGen
    newISBN <- ISBNGen.suchThat(
      generatedISBN => generatedISBN != isbn
    )
  } yield (
    (
      title,
      author,
      isbn,
      description,
      coverImage,
      categories
    ),
    newISBN
  )

  property("the repository is marked as being updated") {
    forAll(databaseGenerator, repositoryGenerator, newISBNDataGenerator) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewISBN
      ) =>
      modifyISBNOfBook(
        database,
        repository,
        bookData
      ) should be a 'success
    }
  }

  property("the updated book is placed into the repository") {
    forAll(databaseGenerator, repositoryGenerator, newISBNDataGenerator) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewISBN
      ) =>
      bookData match {
        case (
          (
            title,
            author,
            _,
            description,
            coverImage,
            categories
          ), newISBN
        ) =>
          modifyISBNOfBook(
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
                association._1 == newISBN
              }.map {
                association =>
                association._2
              }
            )
          val expectedBook =
            TestBook(
              title,
              author,
              newISBN,
              description,
              coverImage,
              categories
            )
          savedBook should equal (expectedBook)
      }
    }
  }

  property("the original book is no longer in the repository") {
    forAll (databaseGenerator, repositoryGenerator, newISBNDataGenerator) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewISBN
      ) =>
      bookData match {
        case ((_, _, isbn, _, _, _), _) =>
          modifyISBNOfBook(
            database,
            repository,
            bookData
          )
          database.removedISBN should equal (isbn)
      }
    }
  }
}
