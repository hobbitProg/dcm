package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.deletingBook

import org.scalatest.PropSpec

/**
  * Specification for removing an existing book from the catalog
  * @author Kyle Cranmer
  * @since 0.3
  */
class DeletingExistingBookSpec
    extends PropSpec {
  property("the book is removed from the catalog")
  property("the book is given to the listeners")
}
