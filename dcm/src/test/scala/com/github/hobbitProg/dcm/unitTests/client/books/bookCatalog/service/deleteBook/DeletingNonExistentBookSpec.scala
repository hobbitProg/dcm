package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.deletBook

import org.scalatest.PropSpec

/**
  * Specification for trying to delete a book that does not exist in the catalog
  * @author Kyle Cranmer
  * @since 0.3
  */
class DeletingNonExistentBookSpec
    extends PropSpec {
  property("indicates the catalog was not updated")(pending)
  property("does not modify the repository")(pending)
  property("does not give the book to the listener")(pending)
}
