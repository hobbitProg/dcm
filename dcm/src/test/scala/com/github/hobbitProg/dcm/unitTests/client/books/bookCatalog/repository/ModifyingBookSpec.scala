package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Prop, Gen}
import Gen.const

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

import scala.collection.Set

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Verifies modifying books within book catalog repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingBookSpec
    extends Specification
    with ScalaCheck {
  sequential

  private type BookDataType = (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories], Titles)
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
    newTitle <- arbitrary[String].suchThat(generatedTitle => generatedTitle != title && generatedTitle.length > 0)
  } yield (title, author, isbn, description, coverImage, categories.toSet, newTitle)

  "Modifying books within the repository" >> {
    "the repository is updated" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, dataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataType) => {
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
              val updateResult =
                repository.update(
                  originalBook,
                  modifiedBook
                )
              updateResult must beRight
          }
        }
      }
    }

    "the updated book is placed into the repository" >> {
      Prop.forAll(databaseGenerator, repositoryGenerator, dataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataType) => {
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
              val updateResult =
                repository.update(
                  originalBook,
                  modifiedBook
                )
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
              ) should beEqualTo(modifiedBook)
          }
        }
      }
    }

    "the original book is no longer in the repository" >> {
      Prop.forAll(databaseGenerator, repositoryGenerator, dataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataType) => {
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
              val updateResult =
                repository.update(
                  originalBook,
                  modifiedBook
                )
              database.removedISBN must beEqualTo(isbn)
          }
        }
      }
    }
  }
}
