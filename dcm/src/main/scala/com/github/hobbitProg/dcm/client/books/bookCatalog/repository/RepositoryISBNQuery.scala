package com.github.hobbitProg.dcm.client.books.bookCatalog.repository

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.ISBNs

/**
  * ISBN query for repository
  * @author Kyle Cranmer
  * since 0.2
  */
class RepositoryISBNQuery(
  private val isbnToQuery: ISBNs
) {
  def isContainedIn(
    repository: BookCatalogRepository
  ): Boolean = {
    repository alreadyContains isbnToQuery
  }
}
