package com.github.hobbitProg.dcm.client.books.bookCatalog.model

class NoBookHasGivenTitleAndAuthor(
  invalidTitle: Titles,
  invalidAuthor: Authors
) extends Exception(
  "Invalid title: " +
    invalidTitle +
    " invalid author: " +
    invalidAuthor
) {
}
