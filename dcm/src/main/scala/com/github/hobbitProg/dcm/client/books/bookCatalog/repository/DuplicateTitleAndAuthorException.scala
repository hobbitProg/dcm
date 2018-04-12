package com.github.hobbitProg.dcm.client.books.bookCatalog.repository

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * The exception indicating a book with the same title and author of another
  * book
  * @author Kyle Cranmer
  * @since 0.2
  */
class DuplicateTitleAndAuthorException(
  title: Titles,
  author: Authors
) extends BookRepositoryError {
}
