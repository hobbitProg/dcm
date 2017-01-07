package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogControl

import java.net.URI

import com.github.hobbitProg.dcm.client.books.Categories
import com.github.hobbitProg.dcm.client.books.bookCatalog.Book
import com.github.hobbitProg.dcm.client.books.bookCatalog.storage.Storage

import scala.collection.Set

/**
  * Populated storage for book catalog window
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookCatalogWindowStorage
  extends Storage {
  // Books in catalog
  var books: Set[Book] =
    Set[Book](
      new Book(
        "Ruins",
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
      new Book(
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
    * Save book into storage
    *
    * @param bookToSave Book to place into storage
    */
  override def save(
    bookToSave: Book
  ): Option[Storage] = {
    books =
      books + bookToSave
    Some(this)
  }

  /**
    * Categories that can be associated with books
    *
    * @return Categories that can be associated with books
    */
  override def definedCategories: Set[Categories] = {
    Set[Categories](
      "sci-fi",
      "conspiracy",
      "fantasy",
      "thriller"
    )
  }

  /**
    * Books that exist in storage
    *
    * @return Books that exist in storage
    */
  override def contents: Set[Book] = {
    books
  }
}
