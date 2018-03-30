package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.model.bookCatalog.modifyingBook

import scala.collection.Set

import org.scalacheck.{Gen, Arbitrary}
import Arbitrary.arbitrary
import Gen.const

import com.github.hobbitProg.dcm.generator.BookGenerators

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._

/**
  * Common functionality to define how book information can be modified to the
  * information associated with another book
  * @author Kyle Cranmer
  * @since 0.2
  */
trait MultipleBookModificationSpec
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
    BookInfoType,
    BookInfoType
  )

  protected var givenOriginalBook: Book = null
  protected var givenUpdatedBook: Book = null

  protected val catalogGenerator = for {
    firstBookInfo <- bookDataGen
    secondBookInfo <- bookDataGen
    catalog <- addBook(
      addBook(
        new BookCatalog(),
        firstBookInfo._1,
        firstBookInfo._2,
        firstBookInfo._3,
        firstBookInfo._4,
        firstBookInfo._5,
        firstBookInfo._6
      ).get,
      secondBookInfo._1,
      secondBookInfo._2,
      secondBookInfo._3,
      secondBookInfo._4,
      secondBookInfo._5,
      secondBookInfo._6
    )
  } yield (
    catalog,
    firstBookInfo,
    secondBookInfo
  )
}
