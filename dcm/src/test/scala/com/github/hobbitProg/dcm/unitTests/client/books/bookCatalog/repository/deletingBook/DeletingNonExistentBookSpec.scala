package com.github.dcm.unitTests.client.books.bookCatalog.repository.deletingBook

import scala.util.{Try, Success}

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Prop, Gen}
import Gen.const

import org.scalatest.{Matchers, PropSpec, TryValues}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.generator.BookGenerators

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.
  database.StubDatabase

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Specification for trying to delete a non-existent book from the book
  * repository
  * @author Kyle Cranmer
  * @since 0.3
  */
class DeletingNonExistentBookSpec
    extends PropSpec
    with BookGenerators
    with DeletingBookSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with TryValues {

  val repositoryDataGenerator = for {
    title <- TitleGen
    author <- AuthorGen
    isbn <- ISBNGen
    description <- DescriptionGen
    coverImage <- CoverImageGen
    categories <- CategoriesGen
    isbnToDelete <- ISBNGen.suchThat(
      generatedISBN =>
      generatedISBN != isbn
    )
    database <- new StubDatabase()
    repository <- (new BookCatalogRepositoryInterpreter()).setConnection(
      database.connectionTransactor
    ).add(
      TestBook(
        title,
        author,
        isbn,
        description,
        coverImage,
        categories
      )
    )
  } yield (
    database,
    repository,
    isbnToDelete
  )

  property("the repository is marked as being unmodified") {
    forAll(repositoryDataGenerator) {
      (repositoryData : Try[RepositoryInfoType]) =>
      val Success(
        (
          _,
          repository,
          isbnToDelete
        )
      ) = repositoryData
      repository.delete(
        isbnToDelete
      ) should be a 'failure
    }
  }

  property("the database is unmodified"){
    forAll(repositoryDataGenerator) {
      (repositoryData : Try[RepositoryInfoType]) =>
      val Success(
        (
          database,
          repository,
          isbnToDelete
        )
      ) = repositoryData
      repository.delete(
        isbnToDelete
      )
      database.removedISBN should be (null)
      database.removedCategoryAssociationISBN should be (null)
    }
  }

}
