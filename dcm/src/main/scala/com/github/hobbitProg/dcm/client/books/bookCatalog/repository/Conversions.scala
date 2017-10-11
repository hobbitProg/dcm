package com.github.hobbitProg.dcm.client.books.bookCatalog.repository

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.{Titles, Authors, ISBNs}

/**
  * Conversions for book catalog repository
  * @author Kyle Cranmer
  * @since 0.2
  */
object Conversions {
  /**
    * Converts title author pair to repository query
    * @param title Title of book to query
    * @param author Author of book to query
    * @return Query to see if book containing given title and author exists
    * within repository
    */
  implicit def bookContaining(
    title: Titles,
    author: Authors
  ): RepositoryTitleAuthorQuery = {
    new RepositoryTitleAuthorQuery(
      title,
      author
    )
  }

  /**
    * Converts isbn to repository query
    * @param isbn ISBN of book to query
    * @return Query to see if book containing given ISBN exists within
    * repository
    */
  implicit def bookContaining(
    isbn: ISBNs
  ): RepositoryISBNQuery =
    new RepositoryISBNQuery(
      isbn
    )
}
