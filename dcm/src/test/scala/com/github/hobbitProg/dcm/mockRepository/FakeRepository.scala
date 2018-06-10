package com.github.hobbitProg.dcm.mockRepository

import scala.util.{Try, Success}

import com.github.hobbitProg.dcm.client.cds.cdCatalog.model._
import com.github.hobbitProg.dcm.client.cds.cdCatalog.repository.
  CDCatalogRepository

/**
  * Repository for testing book catalog service
  * @author Kyle Cranmer
  * @since 0.2
  */
class FakeRepository
    extends CDCatalogRepository {
  /**
    * CD that was placed into repository
    */
  var cdPlacedIntoRepository: CD = _

  /**
    * CD that was removed from repository
    */
  var cdRemovedFromRepository: CD = _

  /**
    * Title of CD already in repository
    */
  var existingTitle: Titles = _

  /**
    * Artist of CD already in repository
    */
  var existingArtist: Artists = _

  /**
    * ISRC of CD already in repository
    */
  var existingISRC: ISRCs = _

  /**
    * ISRC of book that was deleted
    */
  var deletedISRC: ISRCs = _

  /**
    * Add a given CD to the repository
    * @param cd The CD to place into the repository
    * @returns Either the updated repository or an indication that an error
    * occurred in adding the CD
    */
  override def add(
    cd: CD
  ) : Try[CDCatalogRepository] = {
    cdPlacedIntoRepository = cd
    Success(
      this
    )
  }
}
