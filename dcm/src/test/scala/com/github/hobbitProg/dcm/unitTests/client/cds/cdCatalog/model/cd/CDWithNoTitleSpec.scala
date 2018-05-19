package com.github.hobbitProg.dcm.unitTests.client.cd.cdCatalog.model.cd

import scala.collection.Set

import org.scalatest.{PropSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import org.scalacheck.{Arbitrary, Gen, Prop}
import Arbitrary.arbitrary

import cats.scalatest.ValidatedMatchers

import com.github.hobbitProg.dcm.generator.CDGenerators

import com.github.hobbitProg.dcm.client.cds.cdCatalog.model._

/**
  * Specification for trying to create a CD with no title
  * @author Kyle Cranmer
  * @since 0.4
  */
class CDWithNoTitleSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with ValidatedMatchers
    with CDGenerators {

  private class TestCD(
    val title: Titles,
    val artist: Artists,
    val isrc: ISRCs,
    val cover: CoverImages,
    val categories: Set[Categories]
  ) extends CD {}

  private val NoTitleCDGen = for {
    artist <- ArtistGen
    isrc <- ISRCGen
    cover <- CoverImageGen
    categories <- CategoriesGen
  } yield (
    "",
    artist,
    isrc,
    cover,
    categories
  )

  property("No CD is created") {
    forAll (
      NoTitleCDGen
    ) {
      (cdData : CDDataType) =>
      cdData match {
        case (title, artist, isrc, cover, categories) =>
        CD.cd(
          title,
          artist,
          isrc,
          cover,
          categories
        ) should be (invalid)
      }
    }
  }
}
