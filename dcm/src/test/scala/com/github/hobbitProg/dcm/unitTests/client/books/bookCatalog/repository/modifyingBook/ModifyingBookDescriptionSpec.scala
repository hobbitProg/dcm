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
  * Specification for modifying the description of a book within the book
  * repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingBookDescriptionSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers {

  private type BookDataTypeWithNewDescription =
    (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories], Description)

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

  val newDescriptionDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
    newDescription <- Gen.option(arbitrary[String]).suchThat(generatedDescription => generatedDescription != description)
  } yield (title, author, isbn, description, coverImage, categories.toSet, newDescription)

  // Modify the description of a book in the repository
  private def modifyDescriptionOfBook(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataTypeWithNewDescription
  ) : Either[String, Book] =
    bookData match {
      case (title, author, isbn, description, coverImage, categories, newDescription) =>
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
            newDescription,
            coverImage,
            categories
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
    forAll(
      databaseGenerator, repositoryGenerator,
      newDescriptionDataGenerator
    ) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewDescription
      ) =>
      modifyDescriptionOfBook(
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
      newDescriptionDataGenerator
    ) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewDescription
      ) =>
      bookData match {
        case (title, author, isbn, _, coverImage, categories, newDescription) =>
          modifyDescriptionOfBook(
            database,
            repository,
            bookData
          )
          val insertedBook =
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
              newDescription,
              coverImage,
              categories
            )
          insertedBook should equal (expectedBook)
      }
    }
  }
}
