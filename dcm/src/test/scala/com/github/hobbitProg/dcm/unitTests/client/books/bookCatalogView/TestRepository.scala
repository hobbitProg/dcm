package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogView

import java.net.URI

import scala.collection.Set
import scala.util.{Try, Success}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository

/**
  * The repository for verifying the book catalog view
  * @author Kyle Cranmer
  * @since 0.2
  */
class TestRepository
    extends BookCatalogRepository {
  private class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book {
  }

  val existingBooks: Set[Book] =
    Set[Book](
      new TestBook(
        "Goblins",
        "Charles Grant",
        "0061054143",
        Some(
          "Description for goblins"
        ),
        Some(
          getClass.getResource(
            "/Goblins.jpg"
          ).toURI()
        ),
        Set[Categories](
          "Sci-fi",
          "Conspiracy"
        )
      ),
      new TestBook(
        "Ruins",
        "Kevin J. Anderson",
        "0061057363",
        Some(
          "Description for Ruins"
        ),
        Some(
          getClass.getResource(
            "/Ruins.jpg"
          ).toURI()
        ),
        Set[Categories](
          "Sci-fi",
          "Conspiracy"
        )
      )
    )

  /**
    * Add given book to repository
    * @param newBook Book to add to repository
    * @return Disjoint union of either description of error or book that was
    * added to repository
    */
  def add(
    newBook: Book
  ): Try[Book] =
    Success(
      newBook
    )

  /**
    * Modify given book in repository
    * @param originalBook Book that is being modified
    * @param updatedBook Book that has been updated
    * @return Disjoint union of either description of error or updated book
    */
  def update(
    originalBook: Book,
    updatedBook: Book
  ): Try[Book] =
    Success(
      updatedBook
    )

  /**
    * Retrieve book with given ISBN
    * @param isbn ISBN of book to retrieve
    * @return Disjoint union of either description of error or book with given
    * ISBN
    */
  def retrieve(
    isbn: ISBNs
  ): Either[String, Book] =
    Left(
      "Cannot retrieve from test repository"
    )

  /**
    * Retrieve book with given title and author
    * @param title The title of the book to retrieve
    * @param author The author of the book to retrieve
    * @return Disjoint union of either description of erro ro book with given
    * title and author
    */
  def retrieve(
    title: Titles,
    author: Authors
  ): Either[String, Book] =
    Left("Retrieve")

  /**
    * Determine if book with given title and author already exists in storage
    * @param title Title of book that is to be placed into storage
    * @param author Author of book that is to be placed into storage
    * @return True if book with given title and author already exists in
    * storage and false otherwise
    */
  def alreadyContains(
    title: Titles,
    author: Authors
  ): Boolean = false

  /**
    * Determine if book with given ISBN already exists in storage
    * @param isbn ISBN of book that is to be placed into storage
    * @return True if book with given ISBN already exists in storage and false
    * otherwise
    */
  def alreadyContains(
    isbn: ISBNs
  ): Boolean = false

  /**
    * All books that exist within repository
    */
  override def contents: Set[Book] =
    existingBooks

  /**
    * The categories a book can be categoriezed as
    */
  override def definedCategories: Set[Categories] =
    Set[Categories]()
}
