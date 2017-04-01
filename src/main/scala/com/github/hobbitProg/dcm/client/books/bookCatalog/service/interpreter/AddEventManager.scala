package com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter

import akka.actor.Props
import akka.stream.actor.{ActorPublisher, ActorPublisherMessage}

import scala.annotation.tailrec
import scala.collection.Seq

import com.github.hobbitProg.dcm.client.books.bookCatalog.model.Book

/**
  * Manager for books that were added to catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class AddEventManager
    extends ActorPublisher[Book] {
  import ActorPublisherMessage._

  // Buffer of unprocessed books
  var buffer: Seq[Book] =
    Seq[Book]()

  /**
    * Alert subscribers of book that was added to book catalog
    */
  def receive = {
    case newBook: Book =>
      if (buffer.isEmpty && totalDemand > 0) {
        onNext(
          newBook
        )
      }
      else {
        buffer = buffer :+ newBook
        processBooks()
      }
    case Request(_) =>
      processBooks()
    case Cancel =>
      context.stop(
        self
      )
  }

  /**
    * Give unprocessed books to requesting subscribers
    */
  @tailrec final def processBooks(): Unit =
    if (totalDemand > 0) {
      if (totalDemand <= Int.MaxValue) {
        val (booksToProcess, booksLeftToProcess) =
          buffer splitAt totalDemand.toInt
        buffer = booksLeftToProcess
        booksToProcess foreach onNext
      }
      else {
        val (booksToProcess, booksLeftToProcess) =
          buffer splitAt Int.MaxValue
        buffer = booksLeftToProcess
        booksToProcess foreach onNext
        processBooks()
      }
    }


}

object AddEventManager {
  def props: Props =
    Props[AddEventManager]
}
