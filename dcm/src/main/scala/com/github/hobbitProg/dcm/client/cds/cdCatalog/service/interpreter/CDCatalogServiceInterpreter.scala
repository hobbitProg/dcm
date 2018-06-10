package com.github.hobbitProg.dcm.client.cds.cdCatalog.service
package interpreter

import scala.collection.Set
import scala.util.Success

import cats.data.{Kleisli, Validated}
import Validated._

import com.github.hobbitProg.dcm.client.cds.cdCatalog.model._
import CDCatalog._
import com.github.hobbitProg.dcm.client.cds.cdCatalog.repository.
  CDCatalogRepository

/**
  * Interpreter for service handling a catalog containing CDs
  * @author Kyle Cranmer
  * @since 0.4
  */
object CDCatalogServiceInterpreter
    extends CDCatalogService[CDCatalog] {
  /**
    * Add a CD to the given CD catalog
    * @param catalog The catalog being modified
    * @param title The title of the new CD
    * @param artist The artist of the new CD
    * @param isrc The ISRC of the new CD
    * @param cover The cover of the new CD
    * @param categories The categories associated with the CD
    * @return The routine to add the book to the catalog and repository
    */
  def insertCD(
    catalog: CDCatalog,
    title: Titles,
    artist: Artists,
    isrc: ISRCs,
    cover: CoverImages,
    categories: Set[Categories]
  ): CDCatalogOperation[(CDCatalog, CDCatalogRepository)] = Kleisli {
    repository: CDCatalogRepository =>
    val Success(
      updatedCatalog
    ) =
      addCD(
        catalog,
        title,
        artist,
        isrc,
        cover,
        categories
      )
    val Success(
      addedCD
    ) =
      getByISRC(
        updatedCatalog,
        isrc
      )
    val Success(
      updatedRepository
    ) =
      repository.add(
        addedCD
      )
    Valid(
      (
        updatedCatalog,
        updatedRepository
      )
    )
  }
}
