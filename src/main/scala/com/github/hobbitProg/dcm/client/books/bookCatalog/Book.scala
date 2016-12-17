package com.github.hobbitProg.dcm.client.books.bookCatalog

import com.github.hobbitProg.dcm.client.books.{Authors, Categories,
CoverImageLocations, Descriptions, ISBNs, Titles}

import scala.collection.Set

/**
  * Book that exists in catalog
  * @author Kyle Cranmer
  * @since 0.1
  * @constructor Create book associated with catalog
  * @param title Title of book
  * @param author Author of book
  * @param isbn ISBN of book
  * @param description Description of book
  * @param coverImage: Image of cover to book
  * @param categories Categories associated with book
  */
class Book(
            var title: Titles = "",
            var author: Authors = "",
            var isbn: ISBNs = "",
            var description: Descriptions = "",
            var coverImage: CoverImageLocations = None,
            var categories: Set[Categories] = Set[Categories]()
) {
  override def equals(
    obj: Any
   ): Boolean = {
    // Ensure other object is a book
    if (!obj.isInstanceOf[Book]) {
      return false
    }

    // Ensure all fields are the same
    val otherBook: Book = obj.asInstanceOf[Book]

    title == otherBook.title &&
    author == otherBook.author &&
    isbn == otherBook.isbn &&
    description == otherBook.description &&
    coverImage == otherBook.coverImage &&
    categories == otherBook.categories
  }

  override def hashCode(): Int =
    (title +
      author +
      isbn +
      description +
      coverImage +
      categories.toString()).hashCode
}
