package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.modifyingBook

import scala.collection.Set

import cats.data.Validated
import Validated._

import org.scalacheck.{Gen, Arbitrary}
import Arbitrary.arbitrary
import Gen.const

import com.github.hobbitProg.dcm.generator.BookGenerators

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.
  repository.FakeRepository

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
trait ModifyingBookSpec
    extends BookGenerators {

  protected type TwoBookDataType = (
    BookInfoType,
    BookInfoType
  )

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

  protected val catalogGenerator = for {
    catalog <- new BookCatalog
  } yield catalog

  protected val repositoryGenerator = for {
    repository <- new FakeRepository
  } yield repository

  protected val twoBookDataGen = for {
    firstTitle <- TitleGen
    secondTitle <- TitleGen.suchThat(
      generatedTitle =>
      generatedTitle != firstTitle
    )
    firstAuthor <- AuthorGen
    secondAuthor <- AuthorGen.suchThat(
      generatedAuthor =>
      generatedAuthor != firstAuthor
    )
    firstISBN <- ISBNGen
    secondISBN <- ISBNGen.suchThat(
      generatedISBN =>
      generatedISBN != firstISBN
    )
    firstDescription <- DescriptionGen
    secondDescription <- DescriptionGen.suchThat(
      generatedDescription =>
      generatedDescription != firstDescription
    )
    firstCover <- CoverImageGen
    secondCover <- CoverImageGen.suchThat(
      generatedCover =>
      generatedCover != firstCover
    )
    firstCategories <- CategoriesGen
    secondCategories <- CategoriesGen.suchThat(
      generatedCategories =>
      generatedCategories != firstCategories
    )
  } yield (
    (
      firstTitle,
      firstAuthor,
      firstISBN,
      firstDescription,
      firstCover,
      firstCategories
    ),
    (
      secondTitle,
      secondAuthor,
      secondISBN,
      secondDescription,
      secondCover,
      secondCategories
    )
  )

  protected def populateCatalog(
    originalCatalog: BookCatalog,
    repository: FakeRepository,
    bookData: BookInfoType*
  ): BookCatalog =
    bookData.foldLeft(
      originalCatalog
    ){
      (catalog, currentBook) =>
      currentBook match {
        case (
          title,
          author,
          isbn,
          description,
          coverImage,
          categories
        ) =>
          val Valid((populatedCatalog, _)) =
            insertBook(
              catalog,
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
}
