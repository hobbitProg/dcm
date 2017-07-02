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
  // Common variables for creating reactive streams
  implicit val actorFactory =
    ActorSystem()
  implicit val materializer =
    ActorMaterializer()

  // Stream containing new book events
  val newBookPublisher =
    new NewBookPublisher
  val newBookProducer =
    Source.fromPublisher(
      newBookPublisher
    )
  val newBookGraph =
    newBookProducer.toMat(
      BroadcastHub.sink(
        bufferSize = 512
      )
    )(
      Keep.right
    )
  val newBookFlow =
    newBookGraph.run()

  // Stream containing modify book events
  val modifiedBookPublisher =
    new ModifiedBookPublisher
  val modifiedBookProducer =
    Source.fromPublisher(
      modifiedBookPublisher
    )
  val modifiedBookGraph =
    modifiedBookProducer.toMat(
      BroadcastHub.sink(
        bufferSize = 512
      )
    )(
      Keep.right
    )
  val modifiedBookFlow =
    modifiedBookGraph.run()

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
              newBookPublisher publish savedBook
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
    * Replace original version of book with updated version
    * @param originalBook Book that is being modified
    * @param updatedTitle New title of book
    * @param updatedAuthor New author of book
    * @param updatedISBN New ISBN of book
    * @param updatedDescription New description of book
    * @param updatedCover New cover of book
    * @param updatedCategories New categories associated wityh book
    */
  def update(
    originalBook: Book,
    updatedTitle: Titles,
    updatedAuthor: Authors,
    updatedISBN: ISBNs,
    updatedDescription: Description,
    updatedCover: CoverImages,
    updatedCategories: Set[Categories]
  ): Reader[BookRepository, Try[Book]] = {
    Reader {
      repository: BookRepository =>
      // Create book with modified data
      Book.book(
        updatedTitle,
        updatedAuthor,
        updatedISBN,
        updatedDescription,
        updatedCover,
        updatedCategories
      ) match {
        case Valid(modifiedBook) =>
          // Book was created successfully, so replace original book with
          // modified book
          repository.update(
            originalBook,
            modifiedBook
          ) match {
            case Left(error) =>
              // Original book was not replaced with modified book, so indicate
              // book was not updated
              Failure(
                new StoreException(
                  error
                )
              )
            case Right(updatedBook) =>
              // Orignal book was replaced with modified book, so alert all
              // subscribers book was modified
              modifiedBookPublisher publish (
                originalBook,
                modifiedBook
              )
              Success(
                updatedBook
              )
          }
        case Invalid(_) =>
          // Modified book could not be created
          Failure(
            new InvalidBookException()
          )
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
    newBookFlow runForeach {
      newBook =>
      addAction(
        newBook
      )
    }
  }

  /**
    * Register action to perform when book is added to catalog
    * @param modifyAction Action to perform
    */
  def onModify(
    modifyAction: (Book, Book) => Unit
  ): Unit = {
    modifiedBookFlow runForeach {
      modifiedBookData: Tuple2[Book, Book] =>
      modifyAction(
        modifiedBookData._1,
        modifiedBookData._2
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

  /**
    * Determine if book with given ISBN already exists in book catalog
    * @param isbn ISBN of book being examined
    * @return True if book with given ISBN already exists in book catalog and
    * false otherwise
    */
  def existsInCatalog(
    isbn: ISBNs
  ): Reader[BookRepository, Boolean] = {
    Reader {
      repository: BookRepository =>
      repository alreadyContains isbn
    }
  }
}

object BookCatalog extends BookCatalogInterpreter
