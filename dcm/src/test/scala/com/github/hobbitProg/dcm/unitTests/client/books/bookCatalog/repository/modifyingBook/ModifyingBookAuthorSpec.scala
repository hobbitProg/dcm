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
  * Specification for modifying the author of a book within the book repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingBookAuthorSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with TryValues
    with ModifyingAuthorSpec {

  val newAuthorDataGenerator = for {
    title <- TitleGen
    author <- AuthorGen
    isbn <- ISBNGen
    description <- DescriptionGen
    coverImage <- CoverImageGen
    categories <- CategoriesGen
    newAuthor <- AuthorGen.suchThat(
      generatedAuthor => generatedAuthor != author
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
    newAuthor
  )

  property("the repository is marked as being updated") {
    forAll(databaseGenerator, repositoryGenerator, newAuthorDataGenerator) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewAuthor
      ) => {
        modifyAuthorOfBook(
          database,
          repository,
          bookData
        ) should be a 'success
      }
    }
  }

  property("the updated book is placed into the repository") {
    forAll(databaseGenerator, repositoryGenerator, newAuthorDataGenerator) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewAuthor
      ) =>
      bookData match {
        case (
          (
            title,
            _,
            isbn,
            description,
            coverImage,
            categories
          ),
          newAuthor
        ) =>
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
              title,
              newAuthor,
              isbn,
              description,
              coverImage,
              categories
            )
          savedBook should equal (expectedBook)
      }
    }
  }

  property("the original book is no longer is in the repository") {
    forAll(databaseGenerator, repositoryGenerator, newAuthorDataGenerator) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewAuthor
      ) =>
      bookData match {
        case ((_, _, isbn, _, _, _), _) =>
          modifyAuthorOfBook(
            database,
            repository,
            bookData
          )
          database.removedISBN should equal (isbn)
      }
    }
  }
}
