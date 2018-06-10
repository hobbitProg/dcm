package com.github.hobbitProg.dcm.matchers.cdCatalog.scalaTest

import org.scalatest._
import matchers._

import com.github.hobbitProg.dcm.mockRepository.FakeRepository
import com.github.hobbitProg.dcm.client.cds.cdCatalog.model._

/**
  * The matchers for verifying properties about a CD repository
  * @author Kyle Cranmer
  * @since 0.4
  */
trait RepositoryMatchers {
  class RepositoryContainsCDMatcher(
    val expectedCD: CD
  ) extends Matcher[FakeRepository] {
    def apply(
      left : FakeRepository
    ) =
      MatchResult(
        left.cdPlacedIntoRepository == expectedCD,
        "CD does not exist within repository",
        "CD exists within repository"
      )
  }

  def holdCD(expectedCD : CD) =
    new RepositoryContainsCDMatcher(
      expectedCD
    )
}
