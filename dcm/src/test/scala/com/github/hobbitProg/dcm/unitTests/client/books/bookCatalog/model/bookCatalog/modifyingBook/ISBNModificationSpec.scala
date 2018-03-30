package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.modifyingBook

import scala.util.{Try, Success}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Common functionality for modifyuing an ISBN of a book
  * @author Kyle Cranmer
  * &since 0.2
  */
trait ISBNModificationSpec
    extends BookModificationSpec {
  // Modify the ISBN of a book in the catalog
  protected def modifyISBNOfBook(
    catalogData: Try[CatalogInfoType],
    newISBN: ISBNs
  ) : Try[BookCatalog] = {
    val Success((catalog, (title, author, isbn, description, coverImage, categories))) =
      catalogData
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
