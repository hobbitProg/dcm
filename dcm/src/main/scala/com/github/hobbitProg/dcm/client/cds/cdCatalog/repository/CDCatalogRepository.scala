package com.github.hobbitProg.dcm.client.cds.cdCatalog.repository

import scala.util.Try

import com.github.hobbitProg.dcm.client.cds.cdCatalog.model._

/**
  * The algebra for the repository that stores the CD catalog
  * @author Kyle Cranmer
  * @since 0.4
  */
trait CDCatalogRepository {
  /**
    * Add a given CD to the repository
    * @param cd The CD to place into the repository
    * @returns Either the updated repository or an indication that an error
    * occurred in adding the CD
    */
  def add(
    cd: CD
  ) : Try[CDCatalogRepository]
}
