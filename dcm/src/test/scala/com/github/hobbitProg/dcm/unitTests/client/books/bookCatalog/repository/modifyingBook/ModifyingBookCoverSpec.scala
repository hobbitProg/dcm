package com.github.dcm.unitTests.client.books.bookCatalog.repository.modifyingBook

import scala.collection.Set
import scala.util.{Try, Success}

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Prop, Gen}
import Gen.const

import org.scalatest.{Matchers, PropSpec, TryValues}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.database.StubDatabase

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Specification for modifying the cover of a book within the book repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingBookCoverSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with TryValues
    with ModifyingBookSpec {

  private type BookDataTypeWithNewCover =
    (BookInfoType, CoverImages)

  val newCoverImageDataGenerator = for {
    title <- TitleGen
    author <- AuthorGen
    isbn <- ISBNGen
    description <- DescriptionGen
    coverImage <- CoverImageGen
    categories <- CategoriesGen
    newCover <- CoverImageGen.suchThat(
      generatedCover => generatedCover != coverImage
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
    newCover
  )

  // Modify the cover of the book
  private def modifyCoverOfBook(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataTypeWithNewCover
  ) : Try[Book] =
    bookData match {
      case (
        (
          title,
          author,
          isbn,
          description,
          coverImage,
          categories
        ),
        newCover
      ) =>
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
            title,
            author,
            isbn,
            description,
            newCover,
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

  property("the repository is updated") {
    forAll(
      databaseGenerator,
      repositoryGenerator,
      newCoverImageDataGenerator
    ) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewCover
      ) =>
      modifyCoverOfBook(
        database,
        repository,
        bookData
      ) should be a 'success
    }
  }
  property("the updated book is placed into the repository") {
    forAll(
      databaseGenerator,
      repositoryGenerator,
      newCoverImageDataGenerator
    ) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewCover
      ) =>
      bookData match {
        case (
          (
            title,
            author,
            isbn,
            description,
            _,
            categories
          ),
          newCover
        ) =>
          modifyCoverOfBook(
            database,
            repository,
            bookData
          )
          val insertedBook =
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
          val expectedBook =
            TestBook(
              title,
              author,
              isbn,
              description,
              newCover,
              categories
            )
          insertedBook should equal (expectedBook)
      }
    }
  }
}
