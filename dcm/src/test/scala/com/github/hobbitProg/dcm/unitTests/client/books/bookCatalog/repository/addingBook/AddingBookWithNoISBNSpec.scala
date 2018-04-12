package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.addingBook

import scala.collection.Set
import scala.language.implicitConversions
import scala.util.Failure

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Prop, Gen}
import Gen.const

import org.scalatest.{PropSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.database.StubDatabase

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Specification for trying to add a book woth no ISBN to the book repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class AddignBookWithNoISBNSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
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

  val noISBNDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield (title, author, "", description, coverImage, categories.toSet)

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

  property("indicates the repository was not updated") {
    forAll(databaseGenerator, repositoryGenerator, noISBNDataGenerator) {
      (database: StubDatabase, repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataType) =>
      val bookToStore: Book = bookData
      repository.setConnection(
        database.connectionTransactor
      )
      (repository add bookToStore) shouldBe a [Failure[_]]
    }
  }
}
