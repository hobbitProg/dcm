package com.github.hobbitProg.dcm.unitTests.client.books.repository.bookExistence

import scala.collection.immutable.List

import org.scalacheck.{Gen, Arbitrary, Prop}
import Arbitrary._
import Gen.const

import org.scalatest.{PropSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.database.QueryDatabase

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository._
import Conversions._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Specification for finding books in the repository by title and author
  * @author Kyle Cranmer
  * @since 0.2
  */
class TitleAuthorQuerySpec
    extends PropSpec
    with Matchers
    with GeneratorDrivenPropertyChecks {

  private type BookDataType = (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories])

  private type TitleAuthorQueryDataType = (List[BookDataType], Titles, Authors)

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

  private val databaseGenerator = for {
    database <- new QueryDatabase()
  } yield database

  private val repositoryGenerator = for {
    repository <- new BookCatalogRepositoryInterpreter
  } yield repository

  private val isbnGenerator: Gen[ISBNs] = for {
    digit1 <- Gen.choose('0','9')
    digit2 <- Gen.choose('0','9')
    digit3 <- Gen.choose('0','9')
    digit4 <- Gen.choose('0','9')
    digit5 <- Gen.choose('0','9')
    digit6 <- Gen.choose('0','9')
    digit7 <- Gen.choose('0','9')
    digit8 <- Gen.choose('0','9')
    digit9 <- Gen.choose('0','9')
    digit10 <- Gen.choose('0','9')
  } yield List(digit1, digit2, digit3, digit4, digit5, digit6, digit7, digit8, digit9, digit10).mkString

  private val arbitraryISBN: Arbitrary[ISBNs] =
    Arbitrary(isbnGenerator)

  private val dataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitraryISBN.arbitrary
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield ((title, author, isbn, description, coverImage, categories.toSet))

  private val successfulTitleAuthorMatchGenerator = for {
    existingBookData <- Gen.listOfN(25, dataGenerator)
    dataToQuery <- Gen.oneOf(existingBookData)
  } yield (existingBookData, dataToQuery._1, dataToQuery._2)

  private val unsucessfulTitleAuthorMatchGenerator = for {
    existingBookData <- Gen.listOfN(26, dataGenerator)
    dataToQuery <- existingBookData.last
  } yield (existingBookData.init, dataToQuery._1, dataToQuery._2)

  private def populateRepository(
    repository: BookCatalogRepositoryInterpreter,
    availableBooks: List[BookDataType]
  ) : BookCatalogRepositoryInterpreter =
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

  property("determines when a book with a given title and author exists") {
    forAll(
      databaseGenerator,
      repositoryGenerator,
      successfulTitleAuthorMatchGenerator
    ) {
      (
        database: QueryDatabase,
        repository: BookCatalogRepositoryInterpreter,
        queryData: TitleAuthorQueryDataType
      ) =>
      repository setConnection database.connectionTransactor
      queryData match {
        case (availableBooks, matchingTitle, matchingAuthor) =>
          val populatedCatalog =
            populateRepository(
              repository,
              availableBooks
            )
          (bookContaining(matchingTitle, matchingAuthor) isContainedIn
            populatedCatalog) should be (true)
      }
    }
  }

  property("determines when no book exists with a given title and author " +
    "exists") {
    forAll(
      databaseGenerator,
      repositoryGenerator,
      unsucessfulTitleAuthorMatchGenerator
    ) {
      (
        database: QueryDatabase,
        repository: BookCatalogRepositoryInterpreter,
        queryData: TitleAuthorQueryDataType
      ) =>
      queryData match {
        case (availableBooks, differentTitle, differentAuthor) =>
          val populatedCatalog =
            populateRepository(
              repository,
              availableBooks
            )
          (bookContaining(differentTitle, differentAuthor) isContainedIn
            populatedCatalog) should be (false)
      }
    }
  }
}
