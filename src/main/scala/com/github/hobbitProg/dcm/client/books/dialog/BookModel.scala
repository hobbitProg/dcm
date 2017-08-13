package com.github.hobbitProg.dcm.client.books.dialog

import scala.collection.Set

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

class BookModel(
  var title: Titles = "",
  var author: Authors = "",
  var isbn: ISBNs = "",
  var description: Description = None,
  var coverImage: CoverImages = None,
  var categories: Set[Categories] = Set[Categories]()
) {
}
