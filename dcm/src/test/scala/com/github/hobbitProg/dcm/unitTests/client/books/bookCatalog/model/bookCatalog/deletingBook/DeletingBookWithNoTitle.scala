package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.deletingBook


import scala.util.{Try, Success}

import org.scalatest.{PropSpec, TryValues, Matchers}
import org.scalatest.prop._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Specification for trying to delete a book with no title from the catalog
  * @author Kyle Cranmer
  * @since 0.3
  */
class DeletingBookWithNoTitle
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with DeletingBookSpec
    with TryValues {
  protected val catalogGenerator = for {
    bookInfo <- bookDataGen
    otherAuthor <-AuthorGen.suchThat(
      generatedAuthor =>
      generatedAuthor != bookInfo._2
    )
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
    "",
    otherAuthor
  )

  property("the catalog is not modified"){
    forAll(catalogGenerator) {
      (catalogData: Try[CatalogInfoType]) =>
      val Success(
        (
          catalog,
          title,
          author
        )
      ) = catalogData

      deleteBook(
        catalog,
        title,
        author
      ) should be a 'failure
    }
  }

  property("no book is given to the listener"){
    forAll(catalogGenerator) {
      (catalogData: Try[CatalogInfoType]) =>
      val Success(
        (
          catalog,
          title,
          author
        )
      ) = catalogData
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
      givenDeletedBook should be (null)
    }
  }
}
