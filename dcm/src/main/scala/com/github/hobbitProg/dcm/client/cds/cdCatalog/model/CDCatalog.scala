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
  private val catalog: Set[CD] =
    Set[CD](),
  private val addSubscribers: Seq[CD => Unit] =
    Seq[CD => Unit]()
) {
}

object CDCatalog {
  /**
    * Add the given book to the given catalog
    * @param catalog The catalog to update
    * @param title The title of the new CD
    * @param artist The artist that created the new CD
    * @param isrc The ISRC of the new CD
    * @param cover The cover of the new CD
    * @param categories The categorization of the new CD
    * @return Either the updated repository or the reason why the catalog could
    * not be updated
    */
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

  /**
    * Add a listener for add events
    * @param catalog The catalog to update
    * @param listener The listener to add to the catalog
    * @return The catalog with the new listener
    */
  def onAdd(
    catalog: CDCatalog,
    listener: CD => Unit
  ) =
    catalog.copy(
      addSubscribers =
        catalog.addSubscribers :+ listener
    )

  /**
    * Determine if a CD with the given ISRC exists in the given catalog
    * @param catalog The CD catalog to search
    * @param desiredISRC The ISRC of the desired CD
    * @return True if a CD with the given ISRC exists in the  catalog and false
    * otherwise
    */
  def exists(
    catalog: CDCatalog,
    desiredISRC: ISRCs
  ): Boolean =
    catalog.catalog exists {
      existingCD =>
      existingCD.isrc == desiredISRC
    }

  /**
    * Retrieve the given CD with the given ISRC
    * @param catalog The CD catalog to search
    * @param desiredISRC The ISRC of the desired CDcatalog
    * @returns Either the CD with the given ISRC if there is a CD with the ISRC
    * in the catalog and an indication that no CD has the ISRC otherwise
    */
  def getByISRC(
    catalog: CDCatalog,
    desiredISRC: ISRCs
  ): Try[CD] =
    catalog.catalog.find(
      currentCD =>
      currentCD.isrc == desiredISRC
    ) match {
      case Some(correspondingCD) =>
        Success(
          correspondingCD
        )
      case None =>
        Failure(
          new NoCDHasGivenISRC(
            desiredISRC
          )
        )
    }
}
