package com.github.hobbitProg.dcm.client.books.control

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.BookCatalog
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository


/**
  * Window that created book catalog dialog
  * @author Kyle Cranmer
  * @since 0.2
  */
trait BookDialogParent {
  def catalog: BookCatalog
  def repository: BookCatalogRepository
  def catalog_=(
    updatedCatalog: BookCatalog
  ): Unit
  def repository_=(
    updatedRepository: BookCatalogRepository
  ): Unit
}
