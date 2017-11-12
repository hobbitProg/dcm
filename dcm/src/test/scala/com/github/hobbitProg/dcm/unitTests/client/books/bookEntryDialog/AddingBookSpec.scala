package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import org.specs2.mutable.Specification

class AddingBookSpec
    extends Specification {
  "When the user enters data on a book that does not exist in the catalog nor " +
  "the repository, when the data is accepted"  >> {
    "the book is placed into the catalog" >> pending
    "the book is placed into the repository" >> pending
  }

  "When the user enters data on a book (except the title)" >> {
    "the user cannot accept the data" >> pending
  }

  "When the user enters data on a book (except the author)" >> {
    "the user cannot accept the data" >> pending
  }

  "When the user enters data on a book (except the ISBN)" >> {
    "the user cannot accept the data" >> pending
  }

  "When the user enters data on a book (with a title and author that already " +
  "exists in the catalog" >> {
    "the user cannot accept the data" >> pending
  }

  "When the user enters data on a book (with a title and author that already " +
  "exists in the repository" >> {
    "the user cannot accept the data" >> pending
  }

  "When the user enters data on a book (with an ISBN that already exists in " +
  "the catalog)" >> {
    "the user cannot accept the data" >> pending
  }

  "When the user enters data on a book (with an ISBN that already exists in " +
  "the repository)" >> {
    "the user cannot accept the data" >> pending
  }
}
