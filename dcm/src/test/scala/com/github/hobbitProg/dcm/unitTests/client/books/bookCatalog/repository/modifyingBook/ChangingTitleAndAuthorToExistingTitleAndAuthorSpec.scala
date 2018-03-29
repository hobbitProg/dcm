package com.github.dcm.unitTests.client.books.bookCatalog.repository.modifyingBook

import org.scalatest.PropSpec

/**
  * Specification for trying to change the title and author of a book to the
  * title and author of another book in the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ChangingTitleAndAuthorToExistingTitleAndAuthorSpec
    extends PropSpec {
  property("the repository is not updated")(pending)
  property("the updated book was not placed into the repository")
}
