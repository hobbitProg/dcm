package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.modifyingBook

import scala.collection.Set

import org.scalacheck.Gen
import Gen.const

import com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service.repository.FakeRepository

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * Common functionality for specifications for modifying a book using the
  * service
  * @author Kyle Cranmer
  * @since 0.2
  */
trait ModifyingBookSpec {

  protected case class TestBook(
    val title: Titles,
    val author: Authors,
    val isbn: ISBNs,
    val description: Description,
    val coverImage: CoverImages,
    val categories: Set[Categories]
  ) extends Book

  protected val availableCovers =
    Seq(
      "/Goblins.jpg",
      "/GroundZero.jpg",
      "/Ruins.jpg"
    ).map(
      image =>
      Some(
        getClass().
          getResource(
            image
          ).toURI
      )
    )

  protected val catalogGenerator = for {
    catalog <- new BookCatalog
  } yield catalog

  protected val repositoryGenerator = for {
    repository <- new FakeRepository
  } yield repository
}
