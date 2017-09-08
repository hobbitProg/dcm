package com.github.hobbitProg.dcm.client.books.bookCatalog

import java.net.URI

package object model {
  type Titles = String
  type Authors = String
  type ISBNs = String
  type Description = Option[String]
  type CoverImages = Option[URI]
  type Categories = String
}
