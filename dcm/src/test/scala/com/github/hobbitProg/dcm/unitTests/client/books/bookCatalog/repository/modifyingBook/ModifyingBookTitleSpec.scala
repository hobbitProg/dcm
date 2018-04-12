package com.github.dcm.unitTests.client.books.bookCatalog.repository.modifyingBook

import scala.collection.Set
import scala.util.Success

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Prop, Gen}
import Gen.const

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.
  database.StubDatabase

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Specification for modifying the title of a book within the book repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingBookTitleSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with ModifyingTitleSpec {

  val newTitleDataGenerator = for {
    title <- TitleGen
    author <- AuthorGen
    isbn <- ISBNGen
    description <- DescriptionGen
    coverImage <- CoverImageGen
    categories <- CategoriesGen
    newTitle <- TitleGen.suchThat(
      generatedTitle =>
      generatedTitle != title
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
    newTitle
  )

  property("the repository is marked as being updated"){
    forAll(databaseGenerator, repositoryGenerator, newTitleDataGenerator) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewTitle
      ) =>
      modifyTitleOfBook(
        database,
        repository,
        bookData
      ) shouldBe a [Success[_]]
    }
  }

  property("the updated book is placed into the repository"){
    forAll(databaseGenerator, repositoryGenerator, newTitleDataGenerator) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewTitle
      ) =>
      bookData match {
        case
            (
              (
                _,
                author,
                isbn,
                description,
                coverImage,
                categories
              ),
              newTitle
            ) =>
          modifyTitleOfBook(
            database,
            repository,
            bookData
          )
          val bookInRepository =
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
              newTitle,
              author,
              isbn,
              description,
              coverImage,
              categories
            )
          bookInRepository should equal (expectedBook)
      }
    }
  }

  property("the original book is no longer in the repository"){
    forAll(databaseGenerator, repositoryGenerator, newTitleDataGenerator) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewTitle
      ) =>
      bookData match {
        case ((_, _, isbn, _, _, _), _) =>
          modifyTitleOfBook(
            database,
            repository,
            bookData
          )
          database.removedISBN should equal (isbn)
      }
    }
  }
}
