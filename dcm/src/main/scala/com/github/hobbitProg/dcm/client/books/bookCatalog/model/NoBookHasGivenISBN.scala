package com.github.hobbitProg.dcm.client.books.bookCatalog.model

class NoBookHasGivenISBN(
  invalidISBN: ISBNs
) extends Exception(
  "Invalid ISBN: " +
    invalidISBN
) {
}
