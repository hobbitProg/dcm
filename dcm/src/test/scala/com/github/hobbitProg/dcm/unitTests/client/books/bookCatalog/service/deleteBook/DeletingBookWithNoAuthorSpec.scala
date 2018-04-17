package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.deletBook

import org.scalatest.PropSpec

/**
  * Specification for trying to delete a book with no author
  * @author Kyle Cranmer
  * @since 0.3
  */
class DeletingBookWithNoAuthorSpec
    extends PropSpec {
  property("indicates the catalog was not updated")(pending)
  property("does not modify the repository")(pending)
  property("does not give the book to the listener")(pending)
}
