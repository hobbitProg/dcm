package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.modifyingBook

import scala.collection.Set

import cats.data.Validated
import Validated._

import org.scalacheck.Gen
import Gen.const

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.repository.FakeRepository

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.
  BookCatalogServiceInterpreter
import BookCatalogServiceInterpreter._

/**
  * Common functionality for specifications for modifying a book using the
  * service
  * @author Kyle Cranmer
  * @since 0.2
  */
trait ModifyingBookSpec {

  protected type OriginalDataType =
    (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories])

  protected case class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book

  protected var givenOriginalBook: Book = null
  protected var givenUpdatedBook: Book = null

  protected val availableCovers =
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

  protected val catalogGenerator = for {
    catalog <- new BookCatalog
  } yield catalog

  protected val repositoryGenerator = for {
    repository <- new FakeRepository
  } yield repository

  protected def populateCatalog(
    originalCatalog: BookCatalog,
    repository: FakeRepository,
    bookData: OriginalDataType
  ): BookCatalog =
    bookData match {
      case (
        title,
        author,
        isbn,
        description,
        coverImage,
        categories
      ) =>
        val Valid(populatedCatalog) =
          insertBook(
            originalCatalog,
            title,
            author,
            isbn,
            description,
            coverImage,
            categories
          )(
            repository
          )
        populatedCatalog
    }
}
