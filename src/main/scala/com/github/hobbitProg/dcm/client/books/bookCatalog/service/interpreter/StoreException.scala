package com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter

/**
  * Exception indicating book could not be placed into repository
  * @author Kyle Cranmer
  * @since 0.1
  */
class StoreException(
  val errorMessage: String
) extends Exception(
  errorMessage
) {
}
