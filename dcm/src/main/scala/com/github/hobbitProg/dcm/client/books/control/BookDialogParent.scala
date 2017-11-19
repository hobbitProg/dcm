package com.github.hobbitProg.dcm.client.books.control

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.BookCatalog

/**
  * Window that created book catalog dialog
  * @author Kyle Cranmer
  * @since 0.2
  */
trait BookDialogParent {
  def catalog: BookCatalog
  def catalog_=(
    updatedCatalog: BookCatalog
  ): Unit
}
