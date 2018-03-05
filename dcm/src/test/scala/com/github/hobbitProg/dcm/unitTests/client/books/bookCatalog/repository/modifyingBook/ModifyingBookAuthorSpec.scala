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
  * Specification for modifying the author of a book within the book repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingBookAuthorSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers {

  private type BookDataTypeWithNewAuthor =
    (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories], Authors)

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

  val newAuthorDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
    newAuthor <- arbitrary[String].suchThat(generatedAuthor => generatedAuthor != author && generatedAuthor.length > 0)
  } yield (title, author, isbn, description, coverImage, categories.toSet, newAuthor)

  // Modify the author of a book in the repository
  private def modifyAuthorOfBook(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataTypeWithNewAuthor
  ) : Either[String, Book] =
    bookData match {
      case (title, author, isbn, description, coverImage, categories, newAuthor) =>
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
            newAuthor,
            isbn,
            description,
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

  property("the repository is marked as being updated") {
    forAll(databaseGenerator, repositoryGenerator, newAuthorDataGenerator) {
      (database: StubDatabase, repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewAuthor) => {
        modifyAuthorOfBook(
          database,
          repository,
          bookData
        ) should be ('right)
      }
    }
  }

  property("the updated book is placed into the repository") {
    forAll(databaseGenerator, repositoryGenerator, newAuthorDataGenerator) {
      (database: StubDatabase, repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewAuthor) =>
      bookData match {
        case (title, _, isbn, description, coverImage, categories, newAuthor) =>
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
      (database: StubDatabase, repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewAuthor) =>
      bookData match {
        case (_, _, isbn, _, _, _, _) =>
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
