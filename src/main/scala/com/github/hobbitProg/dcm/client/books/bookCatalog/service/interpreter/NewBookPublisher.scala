package com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter

import org.reactivestreams.{Publisher, Subscriber}

import scala.collection.Seq

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.Book

/**
  * Publishes books added to catalog to all subscribers
  * @author Kyle Cranmer
  * @since 0.1
  */
class NewBookPublisher
    extends Publisher[Book] {
  // Objects that need to know what books are added to catalog
  private var subscribers: Seq[Subscriber[_ >: Book]] =
    Seq[Subscriber[_ >: Book]]()

  /**
    * Add subscrber to notify
    * @param newSubscriber New object to notify
    */
  override def subscribe(
    newSubscriber: Subscriber[_ >: Book]
  ): Unit = {
    subscribers =
      subscribers :+ newSubscriber
  }

  /**
    * Let subscribers know new book was added to catalog
    * @param newBook Book that was added to catalog
    */
  def publish(
    newBook: Book
  ): Unit = {
    subscribers foreach {
      subscriber =>
      subscriber onNext newBook
    }
  }
}
