package com.github.hobbitProg.dcm.client.books.bookCatalog.service
package interpreter

import scala.collection.Set
import scala.util.{Success, Failure}

import cats.Id
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
      repository.alreadyContains(title, author) ||
      exists(catalog, isbn) ||
      repository.alreadyContains(isbn)) {
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

  /**
    * Modify a book within the given book catalog
    * @param catalog Catalog being modified
    * @param originalBook Book that already exists in catalog
    * @param newTitle New title of book
    * @param newAuthor New author of book
    * @param newISBN New ISBN of book
    * @param newDescription New description of book
    * @param newCover New cover of book
    * @param newCategories New categories of book
    */
  def modifyBook(
    catalog: BookCatalog,
    originalBook: Book,
    newTitle: Titles,
    newAuthor: Authors,
    newISBN: ISBNs,
    newDescription: Description,
    newCover: CoverImages,
    newCategories: Set[Categories]
  ): BookCatalogOperation[BookCatalog] = Kleisli {
    repository: BookCatalogRepository =>
    updateBook(
      catalog,
      originalBook,
      newTitle,
      newAuthor,
      newISBN,
      newDescription,
      newCover,
      newCategories
    ) match {
      case Success(updatedCatalog) =>
        getByISBN(
          updatedCatalog,
          newISBN
        ) match {
          case Success(newBook) =>
            repository.update(
              originalBook,
              newBook
            ) match {
              case Right(_) =>
                Valid(updatedCatalog)
              case Left(_) =>
                Invalid(
                  BookNotUpdatedWithinCatalog()
                )
            }
          case Failure(_) =>
            Invalid(
              BookNotUpdatedWithinCatalog()
            )
        }
      case Failure(_) =>
        Invalid(
          BookNotUpdatedWithinCatalog()
        )
    }
  }

  /**
    * Determine if book with given title and author exists within catalog
    * @param catalog Catalog being queried
    * @param title Title of book being examined
    * @param author Author of book being examined
    * @return Routine to determine if book exists within either catalog or
    * repository
    */
  def bookExists(
    catalog: BookCatalog,
    title: Titles,
    author: Authors
  ): BookCatalogQuery[Boolean] = Kleisli[Id, BookCatalogRepository, Boolean] {
    repository: BookCatalogRepository =>
    exists(
      catalog,
      title,
      author
    ) ||
    repository.alreadyContains(
      title,
      author
    )
  }

  /**
    * Determine if book with given ISBN exists within catalog
    * @param catalog Catalog being queried
    * @param isbn ISBN of book being examined
    * @return Routine to determine if book exists within either catalog or
    * repository
    */
  def bookExists(
    catalog: BookCatalog,
    isbn: ISBNs
  ): BookCatalogQuery[Boolean] = Kleisli[Id, BookCatalogRepository, Boolean] {
    repository: BookCatalogRepository =>
    exists(
      catalog,
      isbn
    ) ||
    repository.alreadyContains(
      isbn
    )
  }
}
