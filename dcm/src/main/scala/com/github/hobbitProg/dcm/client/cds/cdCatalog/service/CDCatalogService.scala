package com.github.hobbitProg.dcm.client.cds.cdCatalog.service

import scala.collection.Set

import cats.Id
import cats.data.{Kleisli, Validated}

import com.github.hobbitProg.dcm.client.cds.cdCatalog.model._
import com.github.hobbitProg.dcm.client.cds.cdCatalog.repository.
  CDCatalogRepository

/**
  * Service interface for a CD catalog
  * @author Kyle Cranmer
  * @since 0.4
  */
trait CDCatalogService[Catalog] {
  type CDCatalogValidation[ResultType] =
    Validated[CDCatalogError, ResultType]
  type CDCatalogOperation[ResultType] =
    Kleisli[CDCatalogValidation, CDCatalogRepository, ResultType]

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
    catalog: Catalog,
    title: Titles,
    artist: Artists,
    isrc: ISRCs,
    cover: CoverImages,
    categories: Set[Categories]
  ): CDCatalogOperation[(Catalog, CDCatalogRepository)]
}
