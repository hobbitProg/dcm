package com.github.dcm.unitTests.client.books.bookCatalog.repository.modifyingBook

import scala.collection.Set

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Prop, Gen}
import Gen.const

import org.scalatest.{Matchers, PropSpec, EitherValues}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.
  database.StubDatabase

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Specification for setting the title and author of a book to the title and
  * author of another book
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingTitleAndAuthorToTitleAndAuthorOfDifferentBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with EitherValues
    with ModifyingBookSpec {

  type DuplicateTitleAuthorDataType =
    (
      BookInfoType,
      Titles,
      Authors
    )

  val duplicateTitleAndAuthorDataGenerator = for {
    title <- TitleGen
    author <- AuthorGen
    isbn <- ISBNGen
    description <- DescriptionGen
    coverImage <- CoverImageGen
    categories <- CategoriesGen
    newTitle <- TitleGen.suchThat(
      generatedTitle =>
      generatedTitle != title
    )
    newAuthor <- AuthorGen.suchThat(
      generatedAuthor =>
      generatedAuthor != author
    )
  } yield (
    (
      title,
      author,
      isbn,
      description,
      coverImage,
      categories
    ),
    newTitle,
    newAuthor
  )

  // Update the title and author of the given book
  private def updateTitleAndAuthor(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: DuplicateTitleAuthorDataType
  ) : Either[String, Book] =
    bookData match {
      case (
        (
          originalTitle,
          originalAuthor,
          isbn,
          description,
          coverImage,
          categories
        ),
        newTitle,
        newAuthor
      ) =>
        import repository._
        database.existingTitle = newTitle
        database.existingAuthor = newAuthor
        repository.setConnection(
          database.connectionTransactor
        )
        val originalBook : Book =
          TestBook(
            originalTitle,
            originalAuthor,
            isbn,
            description,
            coverImage,
            categories
          )
        val updatedBook : Book =
          TestBook(
            newTitle,
            newAuthor,
            isbn,
            description,
            coverImage,
            categories
          )
        update(
          originalBook,
          updatedBook
        )
    }

  property("an indication that an error occurred is generated") {
    forAll (
      databaseGenerator,
      repositoryGenerator,
      duplicateTitleAndAuthorDataGenerator
    ) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: DuplicateTitleAuthorDataType
      ) =>
      updateTitleAndAuthor(
        database,
        repository,
        bookData
      ) should be ('left)
    }
  }
}
