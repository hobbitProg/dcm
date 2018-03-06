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
  * Specification for trying to remove the title of a book within the book
  * repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class RemovingBookTitleSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers {

  private type BookDataTypeWithNewTitle =
    (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories], Titles)

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

  val emptyTitleDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield (title, author, isbn, description, coverImage, categories.toSet, "")

  // Modify the title of a book in the repository
  private def modifyTitleOfBook(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataTypeWithNewTitle
  ) : Either[String, Book] =
    bookData match {
      case (title, author, isbn, description, coverImage, categories, newTitle) =>
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
            newTitle,
            author,
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

  property("the repository is not updated") {
    forAll(
      databaseGenerator,
      repositoryGenerator,
      emptyTitleDataGenerator
    ) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewTitle
      ) =>
      modifyTitleOfBook(
        database,
        repository,
        bookData
      ) should be ('left)
    }
  }

  property("the database was not updated") {
    forAll(
      databaseGenerator,
      repositoryGenerator,
      emptyTitleDataGenerator
    ) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewTitle
      ) =>
      database.addedTitle = ""
      database.addedAuthor = ""
      database.addedISBN = ""
      database.addedDescription = None
      database.addedCover = None
      database.addedCategoryAssociations =
        Set[(ISBNs, Categories)]()
      bookData match {
        case (_, _, isbn, _, _, _, _) =>
          modifyTitleOfBook(
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
