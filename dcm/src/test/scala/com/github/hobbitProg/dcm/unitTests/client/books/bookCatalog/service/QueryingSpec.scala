package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service

import org.specs2.ScalaCheck
import org.specs2.matcher.Matcher
import org.specs2.mutable.Specification

/**
  * Specification for having the book catalog service determining if a book
  * exists in the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class QueryingSpec
    extends Specification
    with ScalaCheck {
  sequential
  "When the book service is queried to see if a book exists with a given " +
  "title and author" >> {
    "indicates a book exists when a book exists in the catalog with the " +
    "given title and author" >> pending
    "indicates a book exists when a book exists in the repository with the " +
    "given title and author" >> pending
    "indicates no book exists when no book exists in the catalog nor the " +
    "repository with the given title and author" >> pending
  }
}
