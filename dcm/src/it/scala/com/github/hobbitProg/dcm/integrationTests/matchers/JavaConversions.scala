package com.github.hobbitProg.dcm.integrationTests.matchers

import java.util.function.{Predicate => JavaPredicate}

import scala.language.implicitConversions

/**
  * Converts to Java representations
  * @author Kyle Cranmer
  * @since 0.2
  *
  * Based on http://www.michaelpollmeier.com/2014/10/12/calling-java-8-functions-from-scala
  */
object JavaConversions {
  implicit def toJavaPredicate[A](
    scalaPredicate: Function[A, Boolean]
  ) =
    new JavaPredicate[A] {
      override def test(a: A): Boolean = scalaPredicate(a)
    }
}
