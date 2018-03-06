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
  * Specification for modifying the categories of a book within the book
  * repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingBookCategories
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers {

  private type BookDataTypeWithNewCategories =
    (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories], Set[Categories])

  private case class TestBook(
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    coverImage: CoverImages,
    categories: Set[Categories]
  ) extends Book {
  }

  private val availableCovers =
    Seq(
      "/Goblins.jpg",
      "/GroundZero.jpg",
      "/Ruins.jpg"
    ).map(
      image =>
      Some(
        getClass().
          getResource(
            image
          ).toURI
      )
    )

  val databaseGenerator = for {
    database <- new StubDatabase()
  } yield database

  val repositoryGenerator = for {
    repository <- new BookCatalogRepositoryInterpreter
  } yield repository

  val newCategoriesDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
    newCategories <- Gen.listOf(arbitrary[String]).suchThat(generatedCategories => generatedCategories != categories)
  } yield (title, author, isbn, description, coverImage, categories.toSet, newCategories.toSet)

  // Modify the categories of a book in the repository
  private def modifyCategoriesOfBook(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataTypeWithNewCategories
  ) : Either[String, Book] =
    bookData match {
      case (title, author, isbn, description, coverImage, categories, newCategories) =>
        val originalBook =
          TestBook(
            title,
            author,
            isbn,
            description,
            coverImage,
            categories
          )
        val modifiedBook =
          TestBook(
            title,
            author,
            isbn,
            description,
            coverImage,
            newCategories
          )
        repository.setConnection(
          database.connectionTransactor
        )
        repository.update(
          originalBook,
          modifiedBook
        )
    }
  property("the repository is updated") {
    forAll (
      databaseGenerator,
      repositoryGenerator,
      newCategoriesDataGenerator
    ) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewCategories
      ) =>
      modifyCategoriesOfBook(
        database,
        repository,
        bookData
      ) should be ('right)
    }
  }

  property("the updated book is placed into the repository") {
    forAll (
      databaseGenerator,
      repositoryGenerator,
      newCategoriesDataGenerator
    ) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewCategories
      ) =>
      bookData match {
        case (title, author, isbn, description, coverImage, _, newCategories) =>
          modifyCategoriesOfBook(
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
              author,
              isbn,
              description,
              coverImage,
              newCategories
            )
          savedBook should equal (expectedBook)
      }
    }
  }
}
