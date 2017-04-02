package com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl.{BroadcastHub, Keep, Source, SourceQueueWithComplete, RunnableGraph}

import cats.data.Reader
import cats.data.Validated._

import scala.collection.Set
import scala.util.{Try, Success, Failure}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository

/**
  * Interpreter for book catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
object BookCatalogInterpreter extends BookCatalog {
  // Generates actors to generate streams
  implicit val system =
    ActorSystem(
      "BookCatalog"
    )

  // Materializes streams
  implicit val materializer = ActorMaterializer()

  // Queue where add events are placed
  val addEventQueue =
    new NewBookPublisher()

  // Graph for add event stream
  val addEventSource: Source[Book, NotUsed] =
    Source.fromPublisher(
      addEventQueue
    )
  val addEventGraph: RunnableGraph[Source[Book, NotUsed]] =
    addEventSource.toMat(
      BroadcastHub.sink(
        512
      )
    )(
      Keep.right
    )

  // Producer to place add events into
  val addEventProducer =
    addEventGraph.run()

  /**
    * Place new book into catalog
    * @param title Title of new book
    * @param author Author of new book
    * @param isbn ISBN of new book
    * @param description Description of new book
    * @param cover Cover image of new book
    * @param categories Categories of new book
    * @return Function that places book into catalog repository
    */
  def add(
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    cover: CoverImages,
    categories: Set[Categories]
  ): Reader[BookRepository, Try[Book]] = {
    {
      Reader{
        repository: BookRepository =>
        Book.book(
          title,
          author,
          isbn,
          description,
          cover,
          categories
        ) match {
          case Valid(newBook) =>
            repository.save(
              newBook
            ) match {
              case Left(error) =>
                Failure(
                  new StoreException(
                    error
                  )
                )
              case Right(savedBook) =>
                addEventQueue publish savedBook
                Success(
                  savedBook
                )
            }
          case Invalid(_) =>
            Failure(
              new InvalidBookException()
            )
        }
      }
    }
  }

  /**
    * Register action to perform when book is added to catalog
    * @param addAction Action to perform
    */
  def onAdd(
    addAction: Book => Unit
  ): Unit = {
    addEventProducer runForeach {
      addAction
    }
  }
}
