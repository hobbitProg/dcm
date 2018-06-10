package com.github.hobbitProg.dcm.unitTests.client.cds.cdCatalog.repository.addingCD

import scala.collection.Set
import scala.language.implicitConversions
import scala.util.{Try, Success}

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Prop, Gen}
import Gen.const

import org.scalatest.{PropSpec, Matchers, TryValues}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.generator.CDGenerators

import com.github.hobbitProg.dcm.mockDatabase.CDStubDatabase

import com.github.hobbitProg.dcm.client.cds.cdCatalog.model._
import com.github.hobbitProg.dcm.client.cds.cdCatalog.repository.
  CDCatalogRepository
import com.github.hobbitProg.dcm.client.cds.cdCatalog.repository.
  interpreter.CDCatalogRepositoryInterpreter

/**
  * Specification for adding a CD with valid information to the CD repository
  * @author Kyle Cranmer
  * @since 0.4
  */
class AddingValidCDSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with CDGenerators
    with TryValues
    with Matchers  {

  val databaseGenerator = for {
    database <- new CDStubDatabase()
  } yield database

  val repositoryGenerator = for {
    repository <- new CDCatalogRepositoryInterpreter
  } yield repository

  implicit def dataToCD(
    cdData: CDDataType
  ) : CD =
    cdData match {
      case (title, artist, isrc, cover, categories) =>
        TestCD(
          title,
          artist,
          isrc,
          cover,
          categories
        )
    }

  private def addCDToRepository(
    database: CDStubDatabase,
    repository: CDCatalogRepositoryInterpreter,
    cdData: CDDataType
  ) : Try[CDCatalogRepository] = {
    repository setConnection database.connectionTransactor
    repository add cdData
  }

  property("indicates the repository was updated") {
    forAll(
      databaseGenerator,
      repositoryGenerator,
      CDDataGen
    ) {
      (
        database: CDStubDatabase,
        repository: CDCatalogRepositoryInterpreter,
        cdData: CDDataType
      ) =>
      addCDToRepository(
        database,
        repository,
        cdData
      ) should be a 'success
    }
  }

  property("places the CD into the repository") {
    forAll(
      databaseGenerator,
      repositoryGenerator,
      CDDataGen
    ) {
      (
        database: CDStubDatabase,
        repository: CDCatalogRepositoryInterpreter,
        cdData: CDDataType
      ) =>
      addCDToRepository(
        database,
        repository,
        cdData
      )
      val actualCD =
        TestCD(
          database.addedTitle,
          database.addedArtist,
          database.addedISRC,
          database.addedCover,
          database.addedCategoryAssociations.filter(
            association =>
            association._1 == database.addedISRC
          ).map {
            association =>
            association._2
          }
        )
      val expectedCD: CD =
        cdData
      actualCD should be (expectedCD)
    }
  }
}
