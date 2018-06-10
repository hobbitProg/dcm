package com.github.hobbitProg.dcm.unitTests.client.cds.cdCatalog.addingCD

import scala.language.implicitConversions

import cats.data.Validated.Valid
import cats.scalatest.ValidatedMatchers

import org.scalacheck.{Gen, Arbitrary, Prop}
import Arbitrary.arbitrary
import Gen.const

import org.scalatest.{PropSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.github.hobbitProg.dcm.generator.CDGenerators
import com.github.hobbitProg.dcm.matchers.cdCatalog.scalaTest._

import com.github.hobbitProg.dcm.client.cds.cdCatalog.model._
import com.github.hobbitProg.dcm.mockRepository.FakeRepository
import com.github.hobbitProg.dcm.client.cds.cdCatalog.service.interpreter.
  CDCatalogServiceInterpreter
import CDCatalogServiceInterpreter._

/**
  * Specification for adding a new CD with valid information to the catalog
  * using the service
  * @author Kyle Cranmer
  * @since 0.4
  */
class AllValidInformationSpec
    extends PropSpec
    with Matchers
    with GeneratorDrivenPropertyChecks
    with CDGenerators
    with ValidatedMatchers
    with CatalogMatchers
    with RepositoryMatchers {

  private val repositoryGenerator = for {
    repository <- new FakeRepository()
  } yield repository

  property("indicates the CD was added to the catalog") {
    forAll (
      CatalogGenerator,
      repositoryGenerator,
      CDDataGen
    ) {
      (
        catalog: CDCatalog,
        repository: FakeRepository,
        cdData: CDDataType
      ) =>
      cdData match {
        case (title, artist, isrc, coverImage, categories) =>
          insertCD(
            catalog,
            title,
            artist,
            isrc,
            coverImage,
            categories
          )(
            repository
          ) should be (valid)
      }
    }
  }

  implicit def dataToCD(
    data : CDDataType
  ) : CD =
    data match {
      case (
        title,
        artist,
        isrc,
        coverImage,
        categories
      ) =>
        TestCD(
          title,
          artist,
          isrc,
          coverImage,
          categories
        )
    }

  property("places the CD into the catalog") {
    forAll (
      CatalogGenerator,
      repositoryGenerator,
      CDDataGen
    ) {
      (
        catalog: CDCatalog,
        repository: FakeRepository,
        cdData: CDDataType
      ) =>
      cdData match {
        case (title, artist, isrc, coverImage, categories) =>
          val Valid(
            (
              updatedCatalog,
              _
            )
          ) = insertCD(
            catalog,
            title,
            artist,
            isrc,
            coverImage,
            categories
          )(
            repository
          )
          updatedCatalog should containCD(cdData)
      }
    }
  }

  property("places the CD into the repository") {
    forAll (
      CatalogGenerator,
      repositoryGenerator,
      CDDataGen
    ) {
      (
        catalog: CDCatalog,
        repository: FakeRepository,
        cdData: CDDataType
      ) =>
      cdData match {
        case (title, artist, isrc, coverImage, categories) =>
          val Valid(
            (
              _,
              updatedRepository
            )
          ) = insertCD(
            catalog,
            title,
            artist,
            isrc,
            coverImage,
            categories
          )(
            repository
          )
          updatedRepository.asInstanceOf[FakeRepository] should holdCD(cdData)
      }
    }
  }
}
