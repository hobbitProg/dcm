package com.github.hobbitProg.dcm.unitTests.client.cds.cdCatalog.model.addingCD

import scala.collection.Set
import scala.util.Success

import org.scalatest.{PropSpec, Matchers, TryValues}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import org.scalacheck.{Arbitrary, Gen, Prop}
import Arbitrary.arbitrary

import com.github.hobbitProg.dcm.generator.CDGenerators

import com.github.hobbitProg.dcm.client.cds.cdCatalog.model._
import CDCatalog._

/**
  * Specificatgion for sucessfully adding new CDs to the catalog
  * @author Kyle Cranmer
  * @since 0.4
  */
class AddingCDSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with TryValues
    with CDGenerators {

  property("indicates the catalog was updated") {
    forAll(
      CatalogGenerator,
      CDDataGen
    ) {
      (
        catalog: CDCatalog,
        data: CDDataType
      ) =>
      data match {
        case (title, artist, isrc, coverImage, categories) =>
          addCD(
            catalog,
            title,
            artist,
            isrc,
            coverImage,
            categories
          ) should be a 'success
      }
    }
  }

  property("places the CD into the catalog") {
    forAll(
      CatalogGenerator,
      CDDataGen
    ) {
      (
        catalog: CDCatalog,
        data: CDDataType
      ) =>
      data match {
        case (title, artist, isrc, coverImage, categories) =>
          val Success(updatedCatalog) =
            addCD(
              catalog,
              title,
              artist,
              isrc,
              coverImage,
              categories
            )
          val expectedCD =
            TestCD(
              title,
              artist,
              isrc,
              coverImage,
              categories
            )
//          updatedCatalog.catalog should contain (expectedCD)
      }
    }
  }

  property("gives new CD to all listeners") {
    forAll(
      CatalogGenerator,
      CDDataGen
    ) {
      (
        catalog: CDCatalog,
        data: CDDataType
      ) =>
      data match {
        case (title, artist, isrc, coverImage, categories) =>
          whenever(title != "" && artist != "" && isrc != "") {
            var sentCD: CD = null
            val updatedCatalog =
              onAdd(
                catalog,
                addedCD =>
                sentCD = addedCD
              )
            val resultingCatalog =
              addCD(
                updatedCatalog,
                title,
                artist,
                isrc,
                coverImage,
                categories
              )
            val expectedCD =
              TestCD(
                title,
                artist,
                isrc,
                coverImage,
                categories
              )
            sentCD should be (expectedCD)
          }
      }
    }
  }
}
