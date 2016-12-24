package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalogControl

import java.net.URI
import scala.collection.Set

import com.github.hobbitProg.dcm.client.books.Categories
import com.github.hobbitProg.dcm.client.books.bookCatalog.{Book, Catalog}

/**
  * Catalog for book catalog window test
  * @author Kyle Cranmer
  * @since 0.1.
  */
class BookCatalogWindowCatalog
  extends Catalog {
  // Initial books in catalog
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

  // Place book into set when requested
  addStream.listen(
    (newBook: Book) =>
      books = books + newBook
  )

  /**
    * Apply operation to each book in catalog
    *
    * @param op Operation to apply
    */
  override def foreach(
    op: (Book) => Unit
  ): Unit = {
    for (book <- books) {
      op(
        book
      )
    }
  }
}
