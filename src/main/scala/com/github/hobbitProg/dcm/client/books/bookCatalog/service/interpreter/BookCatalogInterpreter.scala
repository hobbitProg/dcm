package com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter

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
}
