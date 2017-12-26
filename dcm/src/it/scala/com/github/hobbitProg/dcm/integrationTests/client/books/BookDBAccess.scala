package com.github.hobbitProg.dcm.integrationTests.client.books

import java.io.File

import scala.collection.Set

import doobie._, doobie.implicits._

import cats._, cats.data._, cats.effect._, cats.implicits._

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * Logic to access the book database for the book catalog integration tests
  * @author Kyle Cranmer
  * @since 0.2
  */
trait BookDBAccess {
  // Create the schema for the book catalog information
  // Performs transactions on book catalog
  protected val bookTransactor =
    Transactor.fromDriverManager[IO](
      BookDBAccess.databaseClass,
      BookDBAccess.databaseURL
    )

  protected def createBookCatalogSchema() = {
    val definedCategoriesSchemaCreationStatement =
      sql"""
        CREATE TABLE definedCategories (
          categoryID integer PRIMARY KEY,
          Category TINYTEXT
        )
      """
    val bookCatalogSchemaCreationStatement =
      sql"""
        CREATE TABLE bookCatalog (
          bookID integer PRIMARY KEY,
          Title MEDIUMTEXT NOT NULL,
          Author MEDIUMTEXT NOT NULL,
          ISBN MEDIUMTEXT NOT NULL,
          Description MEDIUMTEXT,
          Cover MEDIUMTEXT
        );
      """
    val categoryMappingSchemaCreationStatement =
      sql"""
        CREATE TABLE categoryMapping (
          mappingID integer PRIMARY KEY,
          ISBN MEDIUMTEXT,
          Category TINYTEXT
        );
      """
    val schemaCreation =
      for {
        definedCategoriesCreation <- definedCategoriesSchemaCreationStatement.update.run
        bookCatalogCreation <- bookCatalogSchemaCreationStatement.update.run
        categoryMappingCreation <- categoryMappingSchemaCreationStatement.update.run
      } yield definedCategoriesCreation + bookCatalogCreation + categoryMappingCreation
    schemaCreation.transact(
      bookTransactor
    ).unsafeRunSync
  }

  // Remove the file containing the database
  protected def removeDatabaseFile() = {
    val dbFile =
      new File(
        BookDBAccess.databaseFile
      )
    dbFile.delete()
  }

// Add the pre-defined categories to the database
  protected def placePreDefinedCategoriesIntoDatabase() = {
    for (definedCategory <- BookDBAccess.definedCategories) {
      sql"INSERT INTO definedCategories (Category) VALUES ($definedCategory);"
        .update
        .run
        .transact(
          bookTransactor
        ).unsafeRunSync
    }
  }

  // Place the existing books into the database
  protected def placeExistingBooksIntoDatabase() = {
    for (existingBook <- BookDBAccess.existingBooks) {
      val description: String =
        existingBook.description match {
          case Some(definedDescription) =>
            definedDescription
          case None =>
            ""
        }
      val coverImage: String =
        existingBook.coverImage match {
          case Some(definedCover) =>
            definedCover.toString()
          case None =>
            ""
          }
      sql"INSERT INTO bookCatalog(Title,Author,ISBN,Description,Cover)VALUES(${existingBook.title},${existingBook.author},${existingBook.isbn},$description,$coverImage);"
        .update
        .run
        .transact(
          bookTransactor
        ).unsafeRunSync

      for(associatedCategory <- existingBook.categories) {
        sql"INSERT INTO categoryMapping(ISBN,Category)VALUES(${existingBook.isbn},$associatedCategory)"
          .update
          .run
          .transact(
            bookTransactor
          ).unsafeRunSync
      }
    }
  }
}

object BookDBAccess {
  class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book {
  }

  private val databaseFile: String =
    "bookCatalogClient.db"
  private val databaseClass: String =
    "org.sqlite.JDBC"
  private val databaseURL: String =
    "jdbc:sqlite:" + databaseFile

  private val definedCategories: Set[Categories] =
    Set(
      "sci-fi",
      "conspiracy",
      "fantasy",
      "thriller"
    )

  val existingBooks: Set[Book] =
    Set[Book](
      new TestBook(
        "Ruins",
        "Kevin J. Anderson",
        "0061052477",
        Some(
          "Description for Ruins"
        ),
        Some(
          getClass.getResource(
            "/Ruins.jpg"
          ).toURI
        ),
        Set[Categories](
          "sci-fi",
          "conspiracy"
        )
      ),
      new TestBook(
        "Goblins",
        "Charles Grant",
        "0061054143",
        Some(
          "Description for Goblins"
        ),
        Some(
          getClass.getResource(
            "/Goblins.jpg"
          ).toURI()
        ),
        Set[Categories](
          "sci-fi",
          "conspiracy"
        )
      )
    )
}
