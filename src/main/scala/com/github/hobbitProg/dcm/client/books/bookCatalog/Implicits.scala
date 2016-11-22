package com.github.hobbitProg.dcm.client.books.bookCatalog

import scala.collection.Set
import scala.language.implicitConversions

/**
  * Automatic conversions for book client
  */
object Implicits {

  /**
    * Convert tuple containing book data to book
    * @param bookData Data for book
    * @return Book containing book data
    */
  implicit def tupleToBook(
    bookData: (String, String, String, String, String, Set[String])
  ): Book = {
    new Book(
      bookData._1,
      bookData._2,
      bookData._3,
      bookData._4,
      bookData._5,
      bookData._6
    )
  }
}
