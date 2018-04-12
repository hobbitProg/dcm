package com.github.hobbitProg.dcm.client.books.bookCatalog.repository

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

class DuplicateISBNException(
  isbn: ISBNs
) extends BookRepositoryError {
}
