package com.github.hobbitProg.dcm.client.books.bookCatalog.service
package interpreter

import scala.collection.Set
import scala.util.{Success, Failure}

import cats.Id
import cats.data.Kleisli
import cats.data.Validated.{Valid, Invalid}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import BookCatalog._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository

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
  ): BookCatalogOperation[(BookCatalog, BookCatalogRepository)] = Kleisli {
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
      val resultingCatalog =
        for {
          updatedCatalog <- addBook(
            catalog,
            title,
            author,
            isbn,
            description,
            cover,
            categories
          )
          newBook <- getByISBN(
            updatedCatalog,
            isbn
          )
          updatedRepository <- repository.add(
            newBook
          )
        } yield (updatedCatalog, updatedRepository)
      resultingCatalog match {
        case Success(generatedCatalog) =>
          Valid(
            generatedCatalog
          )
        case Failure(_) =>
          Invalid(
            BookNotAddedToRepository()
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
  ): BookCatalogOperation[(BookCatalog, BookCatalogRepository)] = Kleisli {
    repository: BookCatalogRepository => {
      val updatedInfo =
        for {
          updatedCatalog <- updateBook(
            catalog,
            originalBook,
            newTitle,
            newAuthor,
            newISBN,
            newDescription,
            newCover,
            newCategories
          )
          newBook <- getByISBN(
            updatedCatalog,
            newISBN
          )
          updatedRepository <- repository.update(
            originalBook,
            newBook
          )

        } yield (updatedCatalog, updatedRepository)
      updatedInfo match {
        case Success((resultingCatalog, resultingRepository)) =>
          Valid((resultingCatalog, resultingRepository))
        case Failure(_) =>
          Invalid(
            BookNotUpdatedWithinCatalog()
          )
      }
    }
  }

  /**
    * Delete the given book from the catalog
    * @param catalog Catalog being modified
    * @param title The title of the book to remove
    * @param author The author of the book to remove
    * @return Routine to modify book in catalog and repository
    */
  def delete(
    catalog: BookCatalog,
    title: Titles,
    author: Authors
  ): BookCatalogOperation[(BookCatalog, BookCatalogRepository)] = Kleisli {
    repository: BookCatalogRepository =>
    getByTitleAndAuthor(
      catalog,
      title,
      author
    ) match {
      case Success(bookToDelete) =>
        deleteBook(
          catalog,
          title,
          author
        ) match {
          case Success(updatedCatalog) =>
            repository.delete(
              bookToDelete.isbn
            ) match {
              case Success(updatedRepository) =>
                Valid((updatedCatalog, updatedRepository))
              case Failure(_) =>
                Invalid(BookNotRemovedFromRepository())
            }
          case Failure(_) =>
            Invalid(BookNotRemovedFromCatalog())
        }
      case Failure(_) =>
        Invalid(BookNotInCatalog())
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
