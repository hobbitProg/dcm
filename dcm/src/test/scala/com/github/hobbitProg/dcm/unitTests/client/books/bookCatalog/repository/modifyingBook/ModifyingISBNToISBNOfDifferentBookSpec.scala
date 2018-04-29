package com.github.dcm.unitTests.client.books.bookCatalog.repository.modifyingBook

import scala.collection.Set
import scala.util.{Try, Failure}

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Prop, Gen}
import Gen.const

import org.scalatest.{Matchers, PropSpec, TryValues}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.
  database.StubDatabase

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Specification for setting the ISBN of a book to the ISBN of another book
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingISBNToISBNOfDifferentBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with TryValues
    with ModifyingISBNSpec {

  val duplicateISBNDataGenerator = for {
    title <- TitleGen
    author <- AuthorGen
    isbn <- ISBNGen
    description <- DescriptionGen
    coverImage <- CoverImageGen
    categories <- CategoriesGen
    newISBN <- ISBNGen.suchThat(
      generatedISBN =>
      generatedISBN != isbn
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
    newISBN
  )

  property("an indication that an error occurred is generated") {
    forAll(
      databaseGenerator,
      repositoryGenerator,
      duplicateISBNDataGenerator
    ) {
      (
        database: StubDatabase,
        repository: BookCatalogRepositoryInterpreter,
        bookData: BookDataTypeWithNewISBN
      ) =>
      bookData match {
        case (_, newISBN) =>
          database.otherISBN = newISBN
          modifyISBNOfBook(
            database,
            repository,
            bookData
          ) should be a 'failure
      }
    }
  }
}
