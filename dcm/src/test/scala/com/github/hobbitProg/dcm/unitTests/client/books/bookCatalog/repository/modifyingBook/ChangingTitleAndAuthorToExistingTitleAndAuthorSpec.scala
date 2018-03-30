package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.modifyingBook

import scala.util.{Try, Success, Failure}

import org.scalatest.{Matchers, PropSpec, TryValues}
import org.scalatest.prop._

import com.github.hobbitProg.dcm.matchers.bookCatalog.scalatest._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Specification for trying to change the title and author of a book to the
  * title and author of another book in the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ChangingTitleAndAuthorToExistingTitleAndAuthorSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with CatalogMatchers
    with TryValues
    with MultipleBookModificationSpec {

  // Modify the title and author of a book in the catalog
  private def modifyTitleAndAuthorOfBook(
    catalogData: Try[CatalogInfoType]
  ) : Try[BookCatalog] = {
    val Success(
      (
        catalog,
        (
          firstTitle,
          _,
          firstISBN,
          firstDescription,
          firstCoverImage,
          firstCategories
        ),
        (
          secondTitle,
          secondAuthor,
          _,
          _,
          _,
          _
        )
      )
    ) = catalogData
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
        firstISBN
      )
    updateBook(
      catalogWithSubscriber,
      originalBook,
      secondTitle,
      secondAuthor,
      firstISBN,
      firstDescription,
      firstCoverImage,
      firstCategories
    )
  }

  property("the repository is not updated") {
    forAll(catalogGenerator) {
      (catalogData: Try[CatalogInfoType]) =>
      modifyTitleAndAuthorOfBook(
        catalogData
      ) should be a 'failure
    }
  }

  property("the updated book was not placed into the repository") {
    forAll(catalogGenerator) {
      (catalogData: Try[CatalogInfoType]) =>
      modifyTitleAndAuthorOfBook(
        catalogData
      )
      givenOriginalBook should equal (null)
      givenUpdatedBook should equal (null)
    }
  }
}
