package com.github.hobbitProg.dcm.client.books.bookCatalog.service

/**
  * Base type for error that occurred in book catalog service
  */
sealed abstract class BookCatalogError

/**
  * Indication that book cannot be added to catalog
  */
case class BookNotAddedToCatalog() extends BookCatalogError

/**
  * Indication that book cannot be added to repository
  */
case class BookNotAddedToRepository() extends BookCatalogError
