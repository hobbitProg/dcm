package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.modifyingBook

import scala.collection.Set

import org.scalacheck.{Gen, Arbitrary}
import Arbitrary.arbitrary
import Gen.const

import com.github.hobbitProg.dcm.generator.BookGenerators

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Common functionality to define how book information can be modified
  * @author Kyle Cranmer
  * @since 0.2
  */
trait BookModificationSpec
    extends BookGenerators {
  protected case class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book {
  }

  protected type CatalogInfoType = (
    BookCatalog,
    BookInfoType
  )

  protected var givenOriginalBook: Book = null
  protected var givenUpdatedBook: Book = null

  protected val catalogGenerator = for {
    bookInfo <- bookDataGen
    catalog <- addBook(
      new BookCatalog(),
      bookInfo._1,
      bookInfo._2,
      bookInfo._3,
      bookInfo._4,
      bookInfo._5,
      bookInfo._6
    )
  } yield (
    catalog,
    bookInfo
  )
}
