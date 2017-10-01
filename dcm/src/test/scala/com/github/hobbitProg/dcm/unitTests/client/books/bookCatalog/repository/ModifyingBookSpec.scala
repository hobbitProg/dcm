package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

/**
  * Verifies modifying books within book catalog repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingBookSpec
    extends Specification
    with ScalaCheck {
  sequential

  "Modifying books within the repository" >> {
    "the repository is updated" >> pending
    "the updated book is placed into the repository" >> pending
    "the original book is no longer in the repository" >> pending
  }
}
