package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.addingBook

import scala.collection.Set
import scala.language.implicitConversions
import scala.util.{Try, Success}

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Prop, Gen}
import Gen.const

import org.scalatest.{PropSpec, Matchers, TryValues}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.
  database.StubDatabase

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Specification for adding a valid book to a book repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class AddingValidBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with TryValues
    with Matchers {

  private type BookDataType = (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories])

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

  val dataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield (title, author, isbn, description, coverImage, categories.toSet)

  implicit def bookDataToBook(
    bookData: BookDataType
  ) : Book =
    bookData match {
      case (title, author, isbn, description, coverImage, categories) =>
        new TestBook(
          title,
          author,
          isbn,
          description,
          coverImage,
          categories
        )
    }

  // Place the book into the repository
  private def addBookToRepository(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataType
  ) : Try[BookCatalogRepository] = {
    repository.setConnection(
      database.connectionTransactor
    )
    repository add bookData
  }

  property("indicates the repository is updated") {
    forAll(databaseGenerator, repositoryGenerator, dataGenerator) {
      (database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataType) =>
      addBookToRepository(
        database,
        repository,
        bookData
      ) should be a 'success
    }
  }

  property("places the book into the repository") {
    forAll(databaseGenerator, repositoryGenerator, dataGenerator) {
      (database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataType) =>
      addBookToRepository(
        database,
        repository,
        bookData
      )
      val storedBook =
        new TestBook(
          database.addedTitle,
          database.addedAuthor,
          database.addedISBN,
          database.addedDescription,
          database.addedCover,
          database.addedCategoryAssociations.filter(
            association =>
            association._1 == database.addedISBN
          ).map {
            association =>
            association._2
          }
        )
      val expectedBook: Book =
        bookData
      storedBook should equal (expectedBook)
    }
  }
}
