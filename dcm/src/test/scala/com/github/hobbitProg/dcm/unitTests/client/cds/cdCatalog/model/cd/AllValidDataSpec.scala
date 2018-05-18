package com.github.hobbitProg.dcm.unitTests.client.cd.cdCatalog.model.cd

import scala.collection.Set

import cats.data.Validated.Valid

import org.scalatest.{PropSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import org.scalacheck.{Arbitrary, Gen, Prop}
import Arbitrary.arbitrary

import cats.scalatest.ValidatedMatchers

import com.github.hobbitProg.dcm.generator.CDGenerators

import com.github.hobbitProg.dcm.client.cds.cdCatalog.model._

/**
  * Specification for creating a CD with all valid informaiton
  * @author Kyle Cranmer
  * @since 0.4
  */
class AllValidDataSpec
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

  property("a CD is created") {
    forAll(
      CDDataGen
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
          ) should be (valid)
      }
    }
  }

  property("the created CD has the given information") {
    forAll(
      CDDataGen
    ) {
      (cdData : CDDataType) =>
      cdData match {
        case (title, artist, isrc, cover, categories) =>
          val expectedCD =
            new TestCD(
              title,
              artist,
              isrc,
              cover,
              categories
            )
          val Valid(actualCD) =
            CD.cd(
              title,
              artist,
              isrc,
              cover,
              categories
            )
          actualCD should be (expectedCD)
      }
    }
  }
}
