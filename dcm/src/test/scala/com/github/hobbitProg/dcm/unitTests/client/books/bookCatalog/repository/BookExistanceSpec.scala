package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

/**
  * Verifies repository can be queried to see if book exists in catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class BookExistanceSpec
    extends Specification
    with ScalaCheck {
  sequential

  "Determining if a book exists in the repository with a given title and a " +
  "given author" >> {
    "the repository indicates when a book with a given title and a given " +
    "author exists in the repository" >> pending
    "the repository indicates when no book in the repository has the given " +
    "title and a given author" >> pending
  }

  "Determining if a book exists in the repository with a given ISBN" >> {
    "the repository indicates when a book with a given ISBN exists in the " +
    "repostory" >> pending
    "the repository indicates when no book in the repository has the given " +
    "ISBN" >> pending
  }
}
