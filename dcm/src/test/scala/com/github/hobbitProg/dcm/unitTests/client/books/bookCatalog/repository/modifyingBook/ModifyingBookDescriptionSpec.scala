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
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Specification for modifying the description of a book within the book
  * repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingBookDescriptionSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with TryValues
    with ModifyingBookSpec {

  private type BookDataTypeWithNewDescription =
    (BookInfoType, Description)

  val newDescriptionDataGenerator = for {
    title <- TitleGen
    author <- AuthorGen
    isbn <- ISBNGen
    description <- DescriptionGen
    coverImage <- CoverImageGen
    categories <- CategoriesGen
    newDescription <- DescriptionGen.suchThat(
      generatedDescription => generatedDescription != description
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
    newDescription
  )

  // Modify the description of a book in the repository
  private def modifyDescriptionOfBook(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataTypeWithNewDescription
  ) : Try[BookCatalogRepository] =
    bookData match {
      case (
        (
          title,
          author,
          isbn,
          description,
          coverImage,
          categories,
        ),
        newDescription
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
            newDescription,
            coverImage,
            categories
          )
        database.existingTitle =
          title
        database.existingAuthor =
          author
        database.existingISBN =
          isbn
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
      databaseGenerator, repositoryGenerator,
      newDescriptionDataGenerator
    ) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewDescription
      ) =>
      modifyDescriptionOfBook(
        database,
        repository,
        bookData
      ) should be a 'success
    }
  }

  property("the updated book is placed into the repository") {
    forAll (
      databaseGenerator,
      repositoryGenerator,
      newDescriptionDataGenerator
    ) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewDescription
      ) =>
      bookData match {
        case (
          (
            title,
            author,
            isbn,
            _,
            coverImage,
            categories,
          ),
          newDescription
        ) =>
          modifyDescriptionOfBook(
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
              newDescription,
              coverImage,
              categories
            )
          insertedBook should equal (expectedBook)
      }
    }
  }
}
