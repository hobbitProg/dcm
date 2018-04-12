package com.github.dcm.unitTests.client.books.bookCatalog.repository.modifyingBook

import scala.collection.Set
import scala.util.Failure

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
  * Specification for trying to remove the ISBN of a book within the book
  * repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class RemovingBookISBNSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with ModifyingISBNSpec {

  val emptyISBNDataGenerator = for {
    title <- TitleGen
    author <- AuthorGen
    isbn <- ISBNGen
    description <- DescriptionGen
    coverImage <- CoverImageGen
    categories <- CategoriesGen
  } yield (
    (
      title,
      author,
      isbn,
      description,
      coverImage,
      categories
    ),
    ""
  )

  property("the repository is not updated") {
    forAll(
      databaseGenerator,
      repositoryGenerator,
      emptyISBNDataGenerator
    ) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewISBN
      ) =>
      modifyISBNOfBook(
        database,
        repository,
        bookData
      ) shouldBe a [Failure[_]]
    }
  }

  property("the database was not updated") {
    forAll(
      databaseGenerator,
      repositoryGenerator,
      emptyISBNDataGenerator
    ) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewISBN
      ) =>
      database.addedTitle = ""
      database.addedAuthor = ""
      database.addedISBN = ""
      database.addedDescription = None
      database.addedCover = None
      database.addedCategoryAssociations =
        Set[(ISBNs, Categories)]()
      bookData match {
        case ((_, _, isbn, _, _, _), _) =>
          modifyISBNOfBook(
            database,
            repository,
            bookData
          )
          val updatedBook =
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
          val emptyBook =
            TestBook(
              "",
              "",
              "",
              None,
              None,
              Set[Categories]()
            )
          updatedBook should equal (emptyBook)
      }
    }
  }
}
