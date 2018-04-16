package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.deletingBook

import scala.util.{Try, Success}

import org.scalatest.{PropSpec, TryValues, Matchers}
import org.scalatest.prop._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Specification for removing an existing book from the catalog
  * @author Kyle Cranmer
  * @since 0.3
  */
class DeletingExistingBookSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with DeletingBookSpec
    with TryValues {

  protected val catalogGenerator = for {
    bookInfo <- bookDataGen
    catalog <- addBook(
      new BookCatalog(),
      bookInfo._1,
      bookInfo._2,
      bookInfo._3,
      bookInfo._4,
      bookInfo._5,
      bookInfo._6
    )
  } yield (
    catalog,
    bookInfo._1,
    bookInfo._2
  )

  property("the book is removed from the catalog") {
    forAll(catalogGenerator) {
      (catalogData: Try[CatalogInfoType]) =>
      val Success(
        (
          catalog,
          title,
          author
        )
      ) =
        catalogData

      val removeResult =
        deleteBook(
          catalog,
          title,
          author
        )
      removeResult should be a 'success

      val Success(
        updatedCatalog
      ) = removeResult

      exists(
        updatedCatalog,
        title,
        author
      ) should be (false)
    }
  }

  property("the book is given to the listeners") {
    forAll(catalogGenerator) {
      (catalogData: Try[CatalogInfoType]) =>
      val Success(
        (
          catalog,
          title,
          author
        )
      ) =
        catalogData

      givenDeletedBook = null
      val catalogWithListener =
        onDelete(
          catalog,
          deletedBook =>
          givenDeletedBook = deletedBook
        )
      deleteBook(
        catalogWithListener,
        title,
        author
      )

      givenDeletedBook.title should be (title)
      givenDeletedBook.author should be (author)
    }
  }
}
