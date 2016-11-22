package com.github.hobbitProg.dcm.client.books.bookCatalog

import java.sql.Connection

/**
  * Interface to book catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
trait Catalog {
  /**
    * Add book to catalog
    * @param bookToAdd Book to add to catalog
    * @return Catalog including new book
    */
  def +(
    bookToAdd: Book
  ): Catalog
}

object Catalog {
  /**
    * Create database implementation of book catalog
    * @param databaseConnection Connection to book catalog database
    * @return Database implementation of book catalog
    */
  def apply(
    databaseConnection: Connection
  ) : Catalog = {
    new DatabaseCatalog(
      databaseConnection
    )
  }
}
