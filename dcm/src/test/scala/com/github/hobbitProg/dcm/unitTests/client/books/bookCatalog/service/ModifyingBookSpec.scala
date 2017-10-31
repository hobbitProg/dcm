package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

class ModifyingBookSpec
    extends Specification
    with ScalaCheck {
  sequential

  "Changing the title of a book in the catalog to a new title that does not " +
  "exist in the catalog" >> {
    "indicates the catalog was updated" >> pending
    "places the updated book in the catalog" >> pending
    "places the updated book in the repository" >> pending
    "gives the updated book to the listener" >> pending
    "removes the original book from the catalog" >> pending
    "removes the original book from the repository" >> pending
    "gives the original book to the listener" >> pending
  }
}
