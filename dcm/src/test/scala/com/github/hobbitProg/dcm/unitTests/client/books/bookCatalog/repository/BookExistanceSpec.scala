package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import scala.language.implicitConversions

import org.scalacheck.{Gen, Arbitrary, Prop}
import Arbitrary._
import Gen.const

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository._
import Conversions._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Verifies repository can be queried to see if book exists in catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class BookExistanceSpec
    extends Specification
    with ScalaCheck {
  sequential

  private type BookDataType = (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories])
  private type TitleAuthorQueryDataType = (List[BookDataType], Titles, Authors)
  private type ISBNQueryDataType = (List[BookDataType], ISBNs)
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
    database <- new QueryDatabase()
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
  } yield ((title, author, isbn, description, coverImage, categories.toSet))

  val successfulTitleAuthorMatchGenerator = for {
    existingBookData <- Gen.listOfN(25, dataGenerator)
    dataToQuery <- Gen.oneOf(existingBookData)
  } yield (existingBookData, dataToQuery._1, dataToQuery._2)

  val successfulISBNMatchGenerator = for {
    existingBookData <- Gen.listOfN(25, dataGenerator)
    dataToQuery <- Gen.oneOf(existingBookData)
  } yield (existingBookData, dataToQuery._3)

  val unsucessfulTitleAuthorMatchGenerator = for {
    existingBookData <- Gen.listOfN(26, dataGenerator)
    dataToQuery <- existingBookData.last
  } yield (existingBookData.init, dataToQuery._1, dataToQuery._2)

  "Determining if a book exists in the repository with a given title and a " +
  "given author" >> {
    "the repository indicates when a book with a given title and a given " +
    "author exists in the repository" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, successfulTitleAuthorMatchGenerator) {
        (database: QueryDatabase, repository: BookCatalogRepositoryInterpreter, queryData: TitleAuthorQueryDataType) => {
          repository setConnection database.connectionTransactor
          queryData match {
            case (availableBooks, matchingTitle, matchingAuthor) =>
              val populatedCatalog =
                availableBooks.foldLeft(
                  repository
                ){
                  (repositoryBeingPopulated, currentBookData) =>
                  currentBookData match {
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
                      repositoryBeingPopulated add bookToStore
                      repositoryBeingPopulated
                  }
                }
              (bookContaining(matchingTitle, matchingAuthor) isContainedIn populatedCatalog) must beTrue
          }
        }
      }
    }

    "the repository indicates when no book in the repository has the given " +
    "title and a given author" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, unsucessfulTitleAuthorMatchGenerator) {
        (database: QueryDatabase, repository: BookCatalogRepositoryInterpreter, queryData: TitleAuthorQueryDataType) => {
          repository setConnection database.connectionTransactor
          queryData match {
            case (availableBooks, differentTitle, differentAuthor) =>
              val populatedCatalog =
                availableBooks.foldLeft(
                  repository
                ){
                  (repositoryBeingPopulated, currentBookData) =>
                  currentBookData match {
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
                      repositoryBeingPopulated add bookToStore
                      repositoryBeingPopulated
                  }
                }
              (bookContaining(differentTitle, differentAuthor) isContainedIn repository) must beFalse
          }
        }
      }
    }
  }

  "Determining if a book exists in the repository with a given ISBN" >> {
    "the repository indicates when a book with a given ISBN exists in the " +
    "repostory" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, successfulISBNMatchGenerator) {
        (database: QueryDatabase, repository: BookCatalogRepositoryInterpreter, queryData: ISBNQueryDataType) => {
          repository setConnection database.connectionTransactor
          queryData match {
            case (availableBooks, matchingISBN) =>
              val populatedCatalog =
                availableBooks.foldLeft(
                  repository
                ){
                  (repositoryBeingPopulated, currentBookData) =>
                  currentBookData match {
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
                      repositoryBeingPopulated add bookToStore
                      repositoryBeingPopulated
                  }
                }
              (bookContaining(matchingISBN) isContainedIn populatedCatalog) must beTrue
          }
        }
      }
    }

    "the repository indicates when no book in the repository has the given " +
    "ISBN" >> pending
  }
}
