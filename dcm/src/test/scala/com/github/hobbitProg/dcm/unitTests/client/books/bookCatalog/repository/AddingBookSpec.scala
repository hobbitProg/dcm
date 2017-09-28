package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Prop, Gen}
import Gen.const

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

import scala.collection.Set
import scala.util.Success

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Verifies adding books to book catalog repository
  */
class AddingBookSpec
    extends Specification
    with ScalaCheck {
  sequential

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

  private val emptyBook =
    TestBook(
      "",
      "",
      "",
      None,
      None,
      Set()
    )
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

  val noTitleDataGenerator = for {
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield ("", author, isbn, description, coverImage, categories.toSet)

  "Adding valid books to the repository" >> {
    "updates the repository" >> {
      Prop.forAll(databaseGenerator, repositoryGenerator, dataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val bookToStore =
                new TestBook(
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              repository.setConnection(
                database.connectionTransactor
              )
              val saveResult =
                repository add bookToStore
              saveResult must beRight
          }
        }
      }
    }

    "places the book into the repository" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, dataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val bookToStore =
                new TestBook(
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              repository.setConnection(
                database.connectionTransactor
              )
              val saveResult =
                repository add bookToStore
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
              ) must beEqualTo(bookToStore)
          }
        }
      }
    }
  }

  "Trying to add books with no title to the repository" >> {
    "indicates the repository was not updated" >> {
      Prop.forAll(databaseGenerator, repositoryGenerator, noTitleDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val bookToStore =
                new TestBook(
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              repository.setConnection(
                database.connectionTransactor
              )
              val saveResult =
                repository add bookToStore
              saveResult must beLeft
          }
        }
      }
    }
  }

  "Trying to add books with no author to the repository" >> {
    "indicates the repository was not updated" >> pending
  }

  "Trying to add books with the same title and author as a book in the repository" >> {
    "indicates the repository was not updated" >> pending
  }

  "Trying to add books with no ISBN to the repository" >> {
    "indicates the repository was not updated" >> pending
  }

  "Trying to add books with the same ISBN as a book in the repository" >> {
    "indicates the repository was not updated" >> pending
  }
}
