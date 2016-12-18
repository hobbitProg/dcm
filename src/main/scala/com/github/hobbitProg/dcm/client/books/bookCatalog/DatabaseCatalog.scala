package com.github.hobbitProg.dcm.client.books.bookCatalog

import java.net.URI
import java.sql.{Connection, ResultSet, Statement}

import scala.language.implicitConversions
import scala.collection.Set

import com.github.hobbitProg.dcm.client.books.{Authors, Titles, Categories,
Descriptions, CoverImageLocations, ISBNs}
import com.github.hobbitProg.dcm.client.books.bookCatalog.Implicits._

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
  private val titleLocation: Int = 1
  private val authorLocation: Int = 2
  private val isbnLocation: Int = 3
  private val descriptionLocation: Int = 4
  private val coverLocation: Int = 5
  private val categoryLocation: Int = 1

  // Convert image location from database to internal representation
  private implicit def databaseToCoverImageLocation(
    locationFromDatabase: String
  ) : CoverImageLocations =
    locationFromDatabase match {
      case "NULL" => None
      case existingLocation => Some[URI](new URI(locationFromDatabase))
    }

  // Convert internal representation of internal location to database
  // representation
  private implicit def coverImageLocationToDatabase(
    locationToDatabase: CoverImageLocations
  ) : String = {
    locationToDatabase match {
      case Some(imageLocation) => imageLocation.toString
      case None => "NULL"
    }
  }

  // Convert description from database to internal format
  private implicit def databaseToDescription(
    databaseDescription: String
  ): Descriptions =
    databaseDescription match {
      case "NULL" => None
      case existingDescription => Some(existingDescription)
    }

  private implicit def descriptionToDatabase(
    descriptionToConvert: Descriptions
  ): String =
    descriptionToConvert match {
      case None => "NULL"
      case Some(realDescription) => realDescription
    }

  // Register action to add book to database
  private val databaseAdditionListener: Catalog.Subscriptions =
    addStream.listen(
      bookToAdd => {
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
      }
    )

  /**
    * Apply operation to each book in catalog
    *
    * @param op Operation to apply
    */
  override def foreach(
    op: (Book) => Unit
  ): Unit = {
    // Gather core book information
    var gatheredBooks: Set[Book] =
      Set[Book]()
    val bookStatement: Statement =
      databaseConnection.createStatement
    val coreBookInfo: ResultSet =
      bookStatement executeQuery
       "SELECT (Title,Author,ISBN,Description,Cover) FROM bookCatalog"

    // Add categories to books
    coreBookInfo.first()
    while (!coreBookInfo.isAfterLast) {
      val associatedCategories: ResultSet =
        bookStatement executeQuery
          "SELECT Category FROM categoryMapping WHERE ISBN='" +
            (coreBookInfo getString isbnLocation) +
            "'"
      var categorySet: Set[Categories] =
        Set[Categories]()
      associatedCategories.first()
      while (!associatedCategories.isAfterLast) {
        categorySet =
          categorySet + (associatedCategories getString categoryLocation)
      }
      val location: CoverImageLocations =
        coreBookInfo getString coverLocation
      val description: Descriptions =
        coreBookInfo getString descriptionLocation
      val newBook: Book =
        (
          coreBookInfo getString titleLocation,
          coreBookInfo getString authorLocation,
          coreBookInfo getString isbnLocation,
          description,
          location,
          categorySet
        )
      gatheredBooks =
        gatheredBooks + newBook
    }

    // Perform operation on books
    for (bookToWorkOn <- gatheredBooks) {
      op(
        bookToWorkOn
      )
    }
  }
}
