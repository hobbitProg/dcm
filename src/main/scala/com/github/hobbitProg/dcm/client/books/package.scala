package com.github.hobbitProg.dcm.client

import java.net.URI

/**
  * Information common for all books
  */
package object books {
  /**
    * Type for title within book
    */
  type Titles = String

  /**
    * Type for author that wrote book
    */
  type Authors = String

  /**
    * Type for ISBN for book
    */
  type ISBNs = String

  /**
    * Type for descriptions of book
    */
  type Descriptions = String

  /**
    * Type for location where cover image is located
    */
  type CoverImageLocations = Option[URI]

  /**
    * Type for categories associated with book
    */
  type Categories = String
}
