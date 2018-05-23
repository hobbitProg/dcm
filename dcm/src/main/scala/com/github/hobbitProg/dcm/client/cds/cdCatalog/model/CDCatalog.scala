package com.github.hobbitProg.dcm.client.cds.cdCatalog.model

import scala.collection.{Set, Seq}
import scala.util.{Try, Success, Failure}

import cats.data.Validated.{Valid, Invalid}

/**
  * Algebra for a catalog containing CDs
  * @author Kyle Cranmer
  * @since 0.4
  */
case class CDCatalog(
  val catalog: Set[CD] =
    Set[CD](),
  val addSubscribers: Seq[CD => Unit] =
    Seq[CD => Unit]()
) {
}

object CDCatalog {
  def addCD(
    catalog: CDCatalog,
    title: Titles,
    artist: Artists,
    isrc: ISRCs,
    cover: CoverImages,
    categories: Set[Categories]
  ) : Try[CDCatalog] = {
    val Valid(newCD) =
      CD.cd(
        title,
        artist,
        isrc,
        cover,
        categories
      )
    catalog.addSubscribers.foreach {
      subscriber =>
      subscriber(
        newCD
      )
    }
    Success(
      catalog.copy(
        catalog =
          catalog.catalog + newCD
      )
    )
  }

  def onAdd(
    catalog: CDCatalog,
    listener: CD => Unit
  ) =
    catalog.copy(
      addSubscribers =
        catalog.addSubscribers :+ listener
    )
}
