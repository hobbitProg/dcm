package com.github.hobbitProg.dcm.unitTests.client.cds.cdCatalog.model.cdCatalog.queryingCatalog

import scala.List
import scala.collection.Set
import scala.language.implicitConversions
import scala.util.{Try, Success}

import org.scalatest.{Matchers, PropSpec, TryValues}
import org.scalatest.prop._

import org.scalacheck.{Gen, Arbitrary}
import Arbitrary.arbitrary
import Gen.const

import com.github.hobbitProg.dcm.generator.CDGenerators
import com.github.hobbitProg.dcm.client.cds.cdCatalog.model._
import CDCatalog._

class SearchingWithISRCSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with TryValues
    with CDGenerators {
  type QueryData = (
    CDCatalog,
    Titles,
    Artists,
    ISRCs,
    CoverImages,
    Set[Categories]
  )
  type UnsuccessfulQueryData = (
    CDCatalog,
    ISRCs
  )

  implicit def dataToCD(
    queryData: QueryData
  ) : CD =
    queryData match {
      case (
        _,
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

  val SuccessfulQueryGenerator =
    for {
      title <- TitleGen
      artist <- ArtistGen
      isrc <- ISRCGen
      coverImage <- CoverImageGen
      categories <- CategoriesGen
      catalog <- addCD(
        new CDCatalog(),
        title,
        artist,
        isrc,
        coverImage,
        categories
      )
    } yield (
      catalog,
      title,
      artist,
      isrc,
      coverImage,
      categories
    )

  val UnsuccessfulQueryGenerator =
    for {
      title <- TitleGen
      artist <- ArtistGen
      isrc <- ISRCGen
      otherISRC <- ISRCGen.suchThat(
        generatedISRC =>
        generatedISRC != isrc
      )
      coverImage <- CoverImageGen
      categories <- CategoriesGen
      catalog <- addCD(
        new CDCatalog(),
        title,
        artist,
        isrc,
        coverImage,
        categories
      )
    } yield (
      catalog,
      otherISRC
    )

  property("indicates when a CD in the catalog has a given ISRC") {
    forAll(SuccessfulQueryGenerator) {
      (queryData : Try[QueryData]) =>
      val Success(
        (
          catalog,
          _,
          _,
          isrc,
          _,
          _
        )
      ) = queryData
      exists(
        catalog,
        isrc
      ) should be (true)
    }
  }

  property("indicates when no CD in the catalog has a given ISRC") {
    forAll(UnsuccessfulQueryGenerator) {
      (queryData : Try[UnsuccessfulQueryData]) =>
      val Success(
        (
          catalog,
          isrc
        )
      ) = queryData
      exists(
        catalog,
        isrc
      ) should be (false)
    }
  }

  property("indicates a CD was retrieved using an ISRC") {
    forAll(SuccessfulQueryGenerator) {
      (queryData : Try[QueryData]) =>
      val Success(
        (
          catalog,
          _,
          _,
          isrc,
          _,
          _
        )
      ) = queryData
      getByISRC(
        catalog,
        isrc
      ) should be a ('success)
    }
  }

  property("retrieves a CD using an ISRC") {
    forAll(SuccessfulQueryGenerator) {
      (queryData : Try[QueryData]) =>
      val Success(
        (
          catalog,
          _,
          _,
          isrc,
          _,
          _
        )
      ) = queryData
      val Success(
        matchingCD
      ) = getByISRC(
        catalog,
        isrc
      )
      val Success(
        originalData
      ) = queryData
      val expectedCD : CD =
        originalData
      matchingCD should be (expectedCD)
    }
  }

  property("indicates when no CD exists with a given ISRC") {
    forAll(UnsuccessfulQueryGenerator) {
      (queryData : Try[UnsuccessfulQueryData]) =>
      val Success(
        (
          catalog,
          isrc
        )
      ) = queryData
      getByISRC(
        catalog,
        isrc
      ) should be a ('failure)
    }
  }
}
