package com.github.hobbitProg.dcm.client.books.bookCatalog.model

/**
  * Exception indicating book could not be created when placing book into catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class InvalidBookException(
  message: String
) extends Exception(
  message
){
}
