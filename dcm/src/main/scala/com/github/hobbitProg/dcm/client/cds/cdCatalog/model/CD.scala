package com.github.hobbitProg.dcm.client.cds.cdCatalog.model

import scala.collection.Set

import cats.data.Validated
import Validated._

/**
  * The data associated with a CDcatalog
  * @author Kyle Cranmer
  * @since 0.4
  */
trait CD {
  def title: Titles
  def artist: Artists
  def isrc: ISRCs
  def cover: CoverImages
  def categories: Set[Categories]

  override def equals(
    obj: Any
  ) : Boolean = {
    // Ensure other object is a CD
    if (!obj.isInstanceOf[CD]) {
      return false
    }

    // Ensure all fields are the same
    val otherCD: CD  = obj.asInstanceOf[CD]

    title == otherCD.title &&
    artist == otherCD.artist &&
    isrc == otherCD.isrc &&
    cover == otherCD.cover &&
    categories == otherCD.categories
  }
}

object CD {
  // Verify given data is valid
  private def isValid(
    title: Titles
  ) : Boolean = {
    title != ""
  }

  /**
    * Create a valid CD
    * @param title The title of the CD
    * @param artist The artist of the CD
    * @param isrc The CD's isrc
    * @param cover An image of the CD's cover
    * @param categories The categories associated with the CD
    * @returns A valid CD
    */
  def cd(
    title: Titles,
    artist: Artists,
    isrc: ISRCs,
    cover: CoverImages,
    categories: Set[Categories]
  ): Validated[String, CD] = {
    if (isValid(
          title
        )) {
      Valid(
        new CDImplementation(
          title,
          artist,
          isrc,
          cover,
          categories
        )
      )
    }
    else {
      Invalid(
        "Given information is invalid for a CD"
      )
    }
  }

  // Implementation of CD information
  private class CDImplementation(
    val title: Titles,
    val artist: Artists,
    val isrc: ISRCs,
    val cover: CoverImages,
    val categories: Set[Categories]
  ) extends CD {}
}
