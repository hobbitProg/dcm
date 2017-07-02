package com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter

import org.reactivestreams.{Publisher, Subscriber}

import scala.collection.Seq

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.Book

/**
  * Publishes books modified within catalog to all subscribers
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifiedBookPublisher
    extends Publisher[Tuple2[Book, Book]] {
  // Objects that need to know what books have been modified within catalog
  private var subscribers: Seq[Subscriber[_ >: Tuple2[Book, Book]]] =
    Seq[Subscriber[_ >: Tuple2[Book, Book]]]()

  /**
    * Add subscriber to notify
    * @param newSubscriber New object to notify
    */
  override def subscribe(
    newSubscriber: Subscriber[_ >: Tuple2[Book, Book]]
  ): Unit = {
    subscribers =
      subscribers :+ newSubscriber
  }

  /**
    * Let subscribers know book was modified within catalog
    * @param originalBook Book that was modified within catalog
    * @param updatedBook Modified version of book
    */
  def publish(
    originalBook: Book,
    updatedBook: Book
  ): Unit = {
    subscribers foreach {
      subscriber =>
      subscriber onNext (
        new Tuple2(
          originalBook,
          updatedBook
        )
      )
    }
  }
}
