package com.github.hobbitProg.dcm.client.books.bookCatalog.repository

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.{Titles, Authors}

/**
  * Title and author query for repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class RepositoryTitleAuthorQuery(
  private val titleToQuery: Titles,
  private val authorToQuery: Authors
) {
  /**
    * Determine if repository contains book with given title and author
    * @param repository Repository to query
    * @return True if repository contains book with given title and author and
    * false otherwise
    */
  def isContainedIn(
    repository: BookCatalogRepository
  ): Boolean = {
    repository.alreadyContains(
      titleToQuery,
      authorToQuery
    )
  }
}
