package com.github.hobbitProg.dcm.client.books.bookCatalog.service

/**
  * Base type for error that occurred in book catalog service
  */
sealed abstract class BookCatalogError

/**
  * Book could not be added to catalog
  */
case class BookNotAddedToCatalog() extends BookCatalogError

/**
  * Book could be added to repository
  */
case class BookNotAddedToRepository() extends BookCatalogError

/**
  * Book was not updated within catalog
  */
case class BookNotUpdatedWithinCatalog() extends BookCatalogError

/**
  * Book was not removed from catalog
  */
case class BookNotRemovedFromCatalog() extends BookCatalogError

/**
  * Book was not removed from repository
  */
case class BookNotRemovedFromRepository() extends BookCatalogError
