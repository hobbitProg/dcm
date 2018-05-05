package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.BookCatalog
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.control.BookDialogParent

/**
  * Test parent window for the add book dialog
  * @author Kyle Cranmer
  * @since 0.2
  */
class TestParent(
  var catalog: BookCatalog,
  var repository: BookCatalogRepository
) extends BookDialogParent {
}
