package com.github.hobbitProg.dcm.client.books.bookCatalog.service
package interpreter

import scala.collection.Set
import scala.util.{Success, Failure}

import cats.data.Kleisli
import cats.data.Validated.{Valid, Invalid}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.BookCatalogRepository

/**
  * Interpreter for service handling catalog containing books
  * @author Kyle Cranmer
  * @since 0.2
  */
object BookCatalogServiceInterpreter
    extends BookCatalogService[BookCatalog] {
  /**
    * Add a book to the given book catalog
    * @param catalog Catalog being modified
    * @param title Title of new book
    * @param author Author of new book
    * @param isbn ISBN of new book
    * @param description Description of new book
    * @param cover Cover of new book
    * @param categories Categories of new book
    * @return Routine to add book to catalog and repository
    */
  def insertBook(
    catalog: BookCatalog,
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    cover: CoverImages,
    categories: Set[Categories]
  ): BookCatalogOperation[BookCatalog] = Kleisli {
    repository: BookCatalogRepository =>
    if (exists(catalog, title, author) ||
      repository.alreadyContains(title, author)) {
      Invalid(
        BookNotAddedToCatalog()
      )
    }
    else {
      addBook(
        catalog,
        title,
        author,
        isbn,
        description,
        cover,
        categories
      ) match {
        case Success(updatedCatalog) =>
          getByISBN(
            updatedCatalog,
            isbn
          ) match {
            case Success(
              newBook
            ) =>
              repository.add(
                newBook
              ) match {
                case Right(_) =>
                  Valid(
                    updatedCatalog
                  )
                case Left(_) =>
                  Invalid(
                    BookNotAddedToRepository()
                  )
              }
            case Failure(_) =>
              Invalid(
                BookNotAddedToRepository()
              )
          }
          Valid(
            updatedCatalog
          )
        case Failure(_) =>
          Invalid(
            BookNotAddedToCatalog()
          )
      }
    }
  }
}
