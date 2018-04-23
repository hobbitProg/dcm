package com.github.hobbitProg.dcm.client.books.bookCatalog.repository

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * The exception indicating a given ISBN is not associated with a book
  * @author Kyle Cranmer
  * @since 0.3
  */
class ISBNDoesNotExistException(
  isbn: ISBNs
) extends BookRepositoryError {
}
