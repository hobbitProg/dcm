package com.github.dcm.unitTests.client.books.bookCatalog.repository.modifyingBook

import scala.util.Try

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.
  database.StubDatabase

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Prop, Gen}
import Gen.const

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Common routines for testing modifying the author of a book in the repository
  * @author Kyle Cranmer
  * @since 0.2
  */
trait ModifyingAuthorSpec
    extends ModifyingBookSpec {
  protected type BookDataTypeWithNewAuthor =
    (BookInfoType, Authors)

  // Modify the author of a book in the repository
  protected def modifyAuthorOfBook(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataTypeWithNewAuthor
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
        newAuthor
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
            newAuthor,
            isbn,
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
