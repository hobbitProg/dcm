package com.github.hobbitProg.dcm.unitTests.client.books.bookEntryDialog

import scala.collection.Seq

import com.github.hobbitProg.dcm.client.books.bookCatalog.{Book, Catalog}

/**
  * Catalog to use in testing book information
  */
class TestCatalog
  extends Catalog {
  /**
    * Apply operation to each book in catalog
    *
    * @param op Operation to apply
    */
  override def foreach(
    op: (Book) => Unit
  ): Unit = {
  }
}
