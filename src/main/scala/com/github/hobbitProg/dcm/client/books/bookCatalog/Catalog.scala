package com.github.hobbitProg.dcm.client.books.bookCatalog

import sodium.{Listener, StreamSink}

import com.github.hobbitProg.dcm.client.books.bookCatalog.storage.Storage

/**
  * Catalog containing book information
  * @author Kyle Cranmer
  * @since 0.1
  */
class Catalog(
  val bookStorage: Storage
) {
  // Stream containing addition books
  protected val addStream: StreamSink[Book] =
    new StreamSink[Book]

  /**
    * Add book to catalog
    * @param bookToAdd Book to add to catalog
    * @return Catalog including new book
    */
  def +(
    bookToAdd: Book
  ): Option[Catalog] = {
    val updatedStorage: Option[Storage] =
      bookStorage save bookToAdd
    updatedStorage match {
      case Some(storageWithNewBook) =>
        addStream.send(
          bookToAdd
        )
        Some(this)
      case None => None
    }
  }

  /**
    * Register action to perform when book is added to catalog
    * @param action Action to perform when book is added to catalog
    */
  def onAdd(
    action: Book => Unit
  ): Catalog.Subscriptions = {
    addStream.listen(
      action
    )
  }

  /**
    * Apply operation to each book in catalog
    * @param op Operation to apply
    */
  def foreach(
    op: (Book) => Unit
  ): Unit = {
    bookStorage.contents.foreach {
      bookToProcess =>
        op(
          bookToProcess
        )
    }
  }
}

object Catalog {
  /**
    * Subscriptions to book catalog events
    */
  type Subscriptions = Listener
}
