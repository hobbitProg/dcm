package com.github.hobbitProg.dcm.client.cds.cdCatalog.repository
package interpreter

import scala.util.{Try, Success}

import cats._
import cats.data._
import Validated._
import cats.implicits._

import doobie._, doobie.implicits._

import cats._, cats.data._, cats.effect._, cats.implicits._

import com.github.hobbitProg.dcm.client.cds.cdCatalog.model._

/**
  * A database implementation of the repository that stores the CD catalog
  * @author Kyle Cranmer
  * @since 0.4
  */
class CDCatalogRepositoryInterpreter
    extends CDCatalogRepository {
  private type CategoryMappingType = (ISRCs, Categories)
  private type CDType = (Titles, Artists, ISRCs, String)

  // Connection to database containing book catalog
  private var databaseConnection: Transactor[IO] = _

  /**
    * Save connection to database containing CD catalog
    * @param connection Connection to database containing CD catalog
    */
  def setConnection(
    connection: Transactor[IO]
  ): CDCatalogRepository = {
    databaseConnection = connection
    this
  }

  /**
    * Add a given CD to the repository
    * @param cd The CD to place into the repository
    * @returns Either the updated repository or an indication that an error
    * occurred in adding the CD
    */
  def add(
    cd: CD
  ) : Try[CDCatalogRepository] = {
    val coverImageToSave =
      cd.cover match {
        case Some(bookCover) => bookCover.toString()
        case None => "NULL"
      }
    val categoriesToInsert =
      cd.categories.map {
        category =>
        new CategoryMappingType(
          cd.isrc,
          category
        )
      }.toList
    val mainCDStatement =
      sql"INSERT INTO cdCatalog(Title,Artist,ISRC,Cover)VALUES(${
        cd.title
      },${
        cd.artist
      },${
        cd.isrc
      },$coverImageToSave);"
    val statementToInsertCategories =
      Update[CategoryMappingType](
        "INSERT INTO categoryMapping(ISRC,Category)VALUES(?,?);"
      )
    val insertStatement =
      for {
        mainCDInsert <- mainCDStatement.update.run
        categoryUpdate <-
        statementToInsertCategories.updateMany(
          categoriesToInsert
        )
      } yield mainCDInsert + categoryUpdate
    insertStatement.transact(
      databaseConnection
    ).unsafeRunSync
    Success(this)
  }
}
