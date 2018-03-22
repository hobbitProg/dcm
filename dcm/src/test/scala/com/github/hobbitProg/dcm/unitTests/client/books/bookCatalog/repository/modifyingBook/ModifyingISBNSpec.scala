package com.github.dcm.unitTests.client.books.bookCatalog.repository.modifyingBook

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.database.StubDatabase

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Prop, Gen}
import Gen.const

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Common routines for testing modifying the ISBN of a book in the repository
  * @author Kyle Cranmer
  * @since 0.2
  */
trait ModifyingISBNSpec
    extends ModifyingBookSpec {

  protected type BookDataTypeWithNewISBN =
    (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories], ISBNs)

  // Modify the ISBN of a book in the repository
  protected def modifyISBNOfBook(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataTypeWithNewISBN
  ) =
    bookData match {
      case (title, author, isbn, description, coverImage, categories, newISBN) =>
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
            newISBN,
            description,
            coverImage,
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
}
