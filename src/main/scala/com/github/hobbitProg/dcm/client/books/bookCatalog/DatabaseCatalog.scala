package com.github.hobbitProg.dcm.client.books.bookCatalog

import java.sql.{Connection, Statement}

/**
  * Database implementation of book catalog
  * @author Kyle Cranmer
  * @since 0.1
  * @constructor Create database implementation of book catalog
  * @param databaseConnection Connection to database implmentation
  */
private class DatabaseCatalog(
  private val databaseConnection: Connection
) extends Catalog {
  /**
    * Add book to catalog
    *
    * @param bookToAdd Book to add to catalog
    * @return Catalog including new book
    */
  override
  def +(bookToAdd: Book): Catalog = {
    // Add main book information
    val bookStatement: Statement =
    databaseConnection.createStatement
    bookStatement.executeUpdate(
      "INSERT INTO bookCatalog(Title,Author,ISBN,Description,Cover)VALUES('" +
        bookToAdd.title +
        "','" +
        bookToAdd.author +
        "','" +
        bookToAdd.isbn +
        "','" +
        bookToAdd.description +
        "','" +
        bookToAdd.coverImage +
        "')"
    )

    // Add all categories associated with book
    bookToAdd.categories.foreach(
      category =>
        bookStatement.executeUpdate(
          "INSERT INTO catetegoryMapping (ISBN,Category)VALUES('" +
            bookToAdd.isbn +
            "','" +
            category +
            "')"
        )
    )

    new DatabaseCatalog(
      databaseConnection
    )
  }
}
