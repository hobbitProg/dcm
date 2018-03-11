package com.github.hobbitProg.dcm.unitTests.client.bookCatalog.model.book

import scala.collection.Set

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * Common functionality for specifying book functionality
  * @author Kyle Cranmer
  * @since 0.2
  */
trait BookSpecification {
  protected type BookDataType = (
    Titles,
    Authors,
    ISBNs,
    Description,
    CoverImages,
    Set[Categories]
  )

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
}
