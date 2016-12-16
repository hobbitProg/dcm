package com.github.hobbitProg.dcm

import java.lang.Runnable
import scala.language.implicitConversions

/**
  * Converts to various java types
  */
object Conversions {
  implicit def functionToRunnable(
    op: Unit
   ): Runnable = {
    new Runnable {
      override def run(): Unit = op
    }
  }
}
