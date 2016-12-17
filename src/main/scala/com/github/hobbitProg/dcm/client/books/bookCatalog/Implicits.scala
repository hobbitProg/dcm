package com.github.hobbitProg.dcm.client.books.bookCatalog

import com.github.hobbitProg.dcm.client.books._

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
    bookData: (Titles, Authors, ISBNs, Descriptions, CoverImageLocations,
      Set[Categories])
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
