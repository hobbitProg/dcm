package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.modifyingBook

import scala.util.{Try, Success, Failure}

import org.scalatest.{Matchers, PropSpec, TryValues}
import org.scalatest.prop._

import com.github.hobbitProg.dcm.matchers.bookCatalog.scalatest._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Specification for trying to change the ISBN of a book to the ISBN of another
  * book in the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ChangingISBNToExistingISBNSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with CatalogMatchers
    with TryValues
    with MultipleBookModificationSpec {
  // Modify the ISBN of a book in the catalog
  protected def modifyISBNOfBook(
    catalog: BookCatalog,
    bookData: BookInfoType,
    newISBN: ISBNs
  ) : Try[BookCatalog] = {
    bookData match {
      case (title, author, isbn, description, coverImage, categories) =>
        val catalogWithSubscriber =
          onModify(
            catalog,
            (originalBook, updatedBook) => {
              givenOriginalBook = originalBook
              givenUpdatedBook = updatedBook
            }
          )
        val Success(originalBook) =
          getByISBN(
            catalogWithSubscriber,
            isbn
          )
        updateBook(
          catalogWithSubscriber,
          originalBook,
          title,
          author,
          newISBN,
          description,
          coverImage,
          categories
        )
    }
  }

  property("the catalog is not updated") {
    forAll(catalogGenerator) {
      (catalogData: Try[CatalogInfoType]) =>
      val Success(
        (
          catalog,
          firstBook,
          secondBook
        )
      ) = catalogData
      modifyISBNOfBook(
        catalog,
        firstBook,
        secondBook._3
      ) should be a 'failure
    }
  }
  property("the original book was not given to the listener") {
    forAll(catalogGenerator) {
      (catalogData: Try[CatalogInfoType]) =>
      val Success(
        (
          catalog,
          firstBook,
          secondBook
        )
      ) = catalogData
      modifyISBNOfBook(
        catalog,
        firstBook,
        secondBook._3
      )
      givenOriginalBook should equal (null)
    }
  }

  property("the updated book was not given to the listener") {
    forAll(catalogGenerator) {
      (catalogData: Try[CatalogInfoType]) =>
      val Success(
        (
          catalog,
          firstBook,
          secondBook
        )
      ) = catalogData
      modifyISBNOfBook(
        catalog,
        firstBook,
        secondBook._3
      )
      givenUpdatedBook should equal (null)
    }
  }
}
