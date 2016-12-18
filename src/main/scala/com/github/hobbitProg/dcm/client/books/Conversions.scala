package com.github.hobbitProg.dcm.client.books

import scala.language.implicitConversions

/**
  * Converts to and from basic book information
  */
object Conversions {
  /**
    * Convert string description to internal description
    * @param original String version of description
    * @return Internal version of description
    */
  implicit def stringToDescription(
    original: String
  ): Descriptions =
    original match {
      case "" => None
      case fullDescription => Some(fullDescription)
    }

  /**
    * Convert internal description to string description
    * @param internal Internal version of description
    * @return String version of description
    */
  implicit def descriptionToString(
    internal: Descriptions
  ): String =
    internal match {
      case Some(description) => description
      case None => ""
    }
}
