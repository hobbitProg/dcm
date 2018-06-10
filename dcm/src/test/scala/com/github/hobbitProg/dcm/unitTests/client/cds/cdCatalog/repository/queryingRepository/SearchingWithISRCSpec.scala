package com.github.hobbitProg.dcm.unitTests.client.cds.cdCatalog.repository.queryingRepository

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

/**
  * Specification for searching for a CD in the repository using its ISRC
  * @author Kyle
  * @since 0.4
  */
class SearchingWithISRCSpec
    extends PropSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with TryValues
    with CDGenerators {
  property("retrieves a CD using an ISRC")(pending)

  property("indicates when no CD exists with a given ISRC")(pending)
}
