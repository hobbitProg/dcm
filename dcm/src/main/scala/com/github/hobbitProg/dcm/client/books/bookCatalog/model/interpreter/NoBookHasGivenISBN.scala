package com.github.hobbitProg.dcm.client.books.bookCatalog.model.interpreter

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.ISBNs

class NoBookHasGivenISBN(
  invalidISBN: ISBNs
) extends Exception(
  "Invalid ISBN: " +
    invalidISBN
) {
}
