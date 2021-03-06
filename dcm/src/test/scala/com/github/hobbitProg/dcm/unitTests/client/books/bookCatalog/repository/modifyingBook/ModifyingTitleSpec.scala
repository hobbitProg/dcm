package com.github.dcm.unitTests.client.books.bookCatalog.repository.modifyingBook

import scala.collection.Set
import scala.util.Try

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository.database.StubDatabase

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter


/**
  * Common routines for modifying a title of a book
  * @author Kyle Cranmer
  * @since 0.2
  */
trait ModifyingTitleSpec
    extends ModifyingBookSpec {
  protected type BookDataTypeWithNewTitle =
    (BookInfoType, Titles)

  // Modify the title of a book in the repository
  protected def modifyTitleOfBook(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataTypeWithNewTitle
  ) : Try[BookCatalogRepository] =
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
        newTitle
      ) =>
        database.existingTitle = title
        database.existingAuthor = author
        database.existingISBN = isbn
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
            newTitle,
            author,
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
