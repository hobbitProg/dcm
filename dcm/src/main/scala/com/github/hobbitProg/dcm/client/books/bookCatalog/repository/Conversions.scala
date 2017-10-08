package com.github.hobbitProg.dcm.client.books.bookCatalog.repository

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.{Titles, Authors}

/**
  * Conversions for book catalog repository
  * @author Kyle Cranmer
  * @since 0.2
  */
object Conversions {
  implicit def bookContaining(
    title: Titles,
    author: Authors
  ): RepositoryTitleAuthorQuery = {
    new RepositoryTitleAuthorQuery(
      title,
      author
    )
  }
}
