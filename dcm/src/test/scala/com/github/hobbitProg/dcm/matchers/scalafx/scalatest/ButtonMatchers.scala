package com.github.hobbitProg.dcm.matchers.scalafx.scalatest

import org.scalatest._
import matchers._

import javafx.scene.Node

import scalafx.Includes._


/**
  * Matchers to verify button properties
  * @author Kyle Cranmer
  * @since 0.2
  */
trait ButtonMatchers {
  /**
    * Deterimines if a given button is disabled
    *
    * @param left Button being examined
    *
    * @return Result indicating if button is disabled
    */
  class DisabledBePropertyMatcher
      extends BePropertyMatcher[Option[Node]] {
    /**
      * Determine if given button is disabled
      *
      * @param left Button being examined
      *
      * @return Match result that indicates if button is disabled
      */
    def apply(
      left: Option[Node]
    ) = BePropertyMatchResult(
      isDisabled(
        left
      ),
      "disabled"
    )

    // Determine if given button is disabled
    private def isDisabled(
      button: Option[Node]
    ): Boolean = button match {
      case Some(selectedButton) =>
        selectedButton.disable.value
      case None => false
    }
  }

  val disabled = new DisabledBePropertyMatcher()
}
