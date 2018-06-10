package com.github.hobbitProg.dcm.matchers.cdCatalog.scalaTest

import scala.util.{Success, Failure}

import org.scalatest._
import matchers._

import com.github.hobbitProg.dcm.client.cds.cdCatalog.model._
import CDCatalog._

/**
  * The matchers for verifying properties about CD catalogs
  * @author Kyle Cranmer
  * @since 0.4
  */
trait CatalogMatchers {
  class CatalogContainsCDMatcher(
    val expectedCD: CD
  ) extends Matcher[CDCatalog] {
    def apply(
      left: CDCatalog
    ) =
      MatchResult(
        catalogContainsCD(
          left
        ),
        "CD does not exist within catalog",
        "CD exists within catalog"
      )

    private def catalogContainsCD(
      catalog: CDCatalog
    ) : Boolean =
      getByISRC(
        catalog,
        expectedCD.isrc
      ) match {
        case Success(matchedCD) =>
          matchedCD == expectedCD
        case Failure(_) => false
      }
  }

  def containCD(expectedCD : CD) =
    new CatalogContainsCDMatcher(
      expectedCD
    )
}
