package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

/**
  * Verifies adding books to book catalog repository
  */
class AddingBookSpec
    extends Specification
    with ScalaCheck {
  "Adding valid books to the repository" >> {
    "updates the repositoryd" >> pending
    "places the book into the repository" >> pending
  }

  "Trying to add books with no title to the repository" >> {
    "indicates the repository was not updated" >> pending
  }

  "Trying to add books with no author to the repository" >> {
    "indicates the repository was not updated" >> pending
  }

  "Trying to add books with the same title and author as a book in the repository" >> {
    "indicates the repository was not updated" >> pending
  }

  "Trying to add books with no ISBN to the repository" >> {
    "indicates the repository was not updated" >> pending
  }

  "Trying to add books with the same ISBN as a book in the repository" >> {
    "indicates the repository was not updated" >> pending
  }
}
