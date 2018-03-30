package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.modifyingBook

import scala.util.{Try, Success}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Common functionality for modifying an author of a book
  * @author Kyle Cranmer
  * @since 0.2
  */
trait AuthorModificationSpec
    extends BookModificationSpec {
  // Modify the author of a book in the catalog
  protected def modifyAuthorOfBook(
    catalogData: Try[CatalogInfoType],
    newAuthor: Authors
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
      newAuthor,
      isbn,
      description,
      coverImage,
      categories
    )
  }
}
