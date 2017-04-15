package com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{BroadcastHub, Keep, RunnableGraph, Source}

import cats.data.Reader
import cats.data.Validated._

import scala.collection.Set
import scala.util.{Try, Success, Failure}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.BookCatalog

/**
  * Interpreter for book catalog
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookCatalogInterpreter extends BookCatalog {
  implicit val actorFactory =
    ActorSystem()
  implicit val materializer =
    ActorMaterializer()
  val publisher =
    new NewBookPublisher
  val producer =
    Source.fromPublisher(
      publisher
    )
  val runnableGraph =
    producer.toMat(
      BroadcastHub.sink(
        bufferSize = 512
      )
    )(
      Keep.right
    )
  val fromProducer =
    runnableGraph.run()

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
  ): Reader[BookRepository, Try[Book]] =
    Reader {
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
              publisher publish savedBook
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

  /**
    * Register action to perform when book is added to catalog
    * @param addAction Action to perform
    */
  def onAdd(
    addAction: Book => Unit
  ): Unit = {
    fromProducer runForeach {
      newBook =>
      addAction(
        newBook
      )
    }
  }

  /**
    * Determine if book with given title and author already exists in book
    * catalog
    * @param title Title of book being examined
    * @param author Author of book being examined
    * @return True if book with given title and author already exists in book
    * catalog and false otherwise
    */
  def existsInCatalog(
    title: Titles,
    author: Authors
  ): Reader[BookRepository, Boolean] =
    Reader {
      repository: BookRepository =>
      repository alreadyContains (
        title,
        author
      )
    }
}

object BookCatalog extends BookCatalogInterpreter
