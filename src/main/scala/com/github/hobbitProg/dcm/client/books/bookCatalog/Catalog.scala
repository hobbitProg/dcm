package com.github.hobbitProg.dcm.client.books.bookCatalog

import java.sql.Connection
import scala.collection.Seq
import sodium.{Listener, StreamSink}

/**
  * Interface to book catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
trait Catalog {
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
  ): Catalog = {
    addStream.send(
      bookToAdd
    )
    this
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
  ): Unit
}

object Catalog {
  /**
    * Subscriptions to book catalog events
    */
  type Subscriptions = Listener

  /**
    * Create database implementation of book catalog
    * @param databaseConnection Connection to book catalog database
    * @return Database implementation of book catalog
    */
  def apply(
    databaseConnection: Connection
  ) : Catalog = {
    new DatabaseCatalog(
      databaseConnection
    )
  }
}
