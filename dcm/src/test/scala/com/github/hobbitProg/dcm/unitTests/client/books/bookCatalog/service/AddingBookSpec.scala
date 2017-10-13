package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

/**
  * Specification for having the book catalog service adding a gook
  * @author Kyle Cranmer
  * @since 0.2
  */
class AddingBookSpec
    extends Specification
    with ScalaCheck {
  "Given a valid book information to add to the catalog" >> {
    "indicates the book was added to the catalog" >> pending
    "places the book into the catalog" >> pending
    "places the book into the repository" >> pending
  }

  "Given book information without a title" >> {
    "indicates the book was not added to the catalog" >> pending
    "does not place the book into the catalog" >> pending
    "does not place the book into the repository" >> pending
  }

  "Given book information without an author" >> {
    "indicates the book was not added to the catalog" >> pending
    "does not place the book into the catalog" >> pending
    "does not place the book into the repository" >> pending
  }

  "Given book information without an ISBN" >> {
    "indicates the book was not added to the catalog" >> pending
    "does not place the book into the catalog" >> pending
    "does not place the book into the repository" >> pending
  }

  "Given book information with a title and author of a book that already " +
  "exists in the catalog" >> {
    "indicates the book was not added to the catalog" >> pending
    "does not place the book into the catalog" >> pending
    "does not place the book into the repository" >> pending
  }

  "Given book information with an ISBN of a book that already exists in the " +
  "catalog" >> {
    "indicates the book was not added to the catalog" >> pending
    "does not place the book into the catalog" >> pending
    "does not place the book into the repository" >> pending
  }
}
