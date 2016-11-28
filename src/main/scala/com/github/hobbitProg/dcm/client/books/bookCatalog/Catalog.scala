package com.github.hobbitProg.dcm.client.books.bookCatalog

import java.sql.Connection
import scala.collection.Set
import sodium.{Listener, StreamSink}

/**
  * Interface to book catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
trait Catalog {
  protected val addStream: StreamSink[Book] =
    new StreamSink[Book]
  private var actionListeners: Set[Listener] =
    Set[Listener]()

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
  ): Unit = {
    actionListeners =
      actionListeners +
      addStream.listen(
        action
      )
  }
}

object Catalog {
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
