package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogControl

import java.net.URI

import scala.collection.Set
import scala.util.{Either, Right}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository

/**
  * Book repository to use in testing the book catalog control
  * @author Kyle Cranmer
  * @since 0.1
  */
class TestRepository
    extends BookRepository {
  private class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book

  /**
    * Save book into repository
    * @param bookToSave Book to place into repository
    * @return A disjoint union of either an error or the book that was added to
    * the repository
    */
  def save(
    bookToSave: Book
  ): Either[String, Book] =
    Right(
      bookToSave
    )

  /**
    * Replace given book with updated copy of book
    * @param originalBook Book that is being updated
    * @param updatedBook Book containing updated information
    * @return A disjoint union of either an error or book with updated
    * information
    */
  def update(
    originalBook: Book,
    updatedBook: Book
  ): Either[String, Book] = {
    Left("Placeholder")
  }

  /**
    * Categories available for books
    */
  def definedCategories: Set[Categories] =
    Set()

  /**
    * All books that exist within repository
    */
  def contents: Set[Book] =
    Set(
      new TestBook(
        "Runs",
        "Kevin J. Anderson",
        "0061052477",
        Some("Description for Ruins"),
        Some[URI](
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
        Some("Description for Goblins"),
        Some[URI](
          getClass.getResource(
            "/Goblins.jpg"
          ).toURI
        ),
        Set[Categories](
          "sci-fi",
          "conspiracy"
        )
      )
    )

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
}
