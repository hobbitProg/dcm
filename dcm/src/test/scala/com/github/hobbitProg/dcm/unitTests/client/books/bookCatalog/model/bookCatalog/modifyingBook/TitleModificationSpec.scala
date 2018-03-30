package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.modifyingBook

import scala.util.{Try, Success, Failure}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Common functionality for specifications about modifying the title of an
  * existing book
  * @author Kyle Cranmer
  * @since 0.2
  */
trait TitleModificationSpec
    extends BookModificationSpec {
  /**
    * Modify the title of a book in the catalog
    * @param catalogData Information on the catalog and the book that exists in
    * the catalog
    * @param newTitle The new title of the book
    */
  protected def modifyTitleOfBook(
    catalogData: Try[CatalogInfoType],
    newTitle: Titles
  ) : Try[BookCatalog] = {
    val Success(
      (
        catalog,
        (
          title,
          author,
          isbn,
          description,
          coverImage,
          categories
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
        isbn
      )
    updateBook(
      catalogWithSubscriber,
      originalBook,
      newTitle,
      author,
      isbn,
      description,
      coverImage,
      categories
    )
  }
}
