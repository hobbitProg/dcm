package com.github.hobbitProg.dcm.client.books.bookCatalog.model

import scala.collection.{Seq, Set}
import scala.util.{Try, Success, Failure}

import cats.data.Validated.{Valid, Invalid}

/**
  * Algebra for catalog containing books
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookCatalog(
  private val catalog: Set[Book] =
    Set[Book](),
  private val addSubscribers: Seq[Book => Unit] =
    Seq[Book => Unit](),
  private val modifySubscribers: Seq[(Book, Book) => Unit] =
    Seq[(Book, Book) => Unit](),
  private val deleteSubscribers: Seq[Book => Unit] =
    Seq[Book => Unit]()
) {
}

object BookCatalog {
  /**
    * Place new book into catalog
    * @param catalog Catalog to place book into
    * @param title Title of new book
    * @param author Author of new book
    * @param isbn ISBN of new book
    * @param description Description of new book
    * @param cover Cover image of new book
    * @param categories Categories of new book
    * @return Indication if book was added to repository
    */
  def addBook(
    catalog: BookCatalog,
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    cover: CoverImages,
    categories: Set[Categories]
  ): Try[BookCatalog] =
    Book.book(
      title,
      author,
      isbn,
      description,
      cover,
      categories
    ) match {
      case Valid(newBook) =>
        for (action <- catalog.addSubscribers) {
          action(
            newBook
          )
        }
        Success(
          new BookCatalog(
            catalog.catalog + newBook,
            catalog.addSubscribers,
            catalog.modifySubscribers,
            catalog.deleteSubscribers
          )
        )
      case Invalid(errorReason) =>
        Failure(
          new InvalidBookException(
            errorReason
          )
        )
    }

  /**
    * Replace original version of book with updated version
    * @param catalog Catalog containing book to update
    * @param originalBook Book that is being modified
    * @param updatedTitle New title of book
    * @param updatedAuthor New author of book
    * @param updatedISBN New ISBN of book
    * @param updatedDescription New description of book
    * @param updatedCover New cover of book
    * @param updatedCategories New categories associated wityh book
    */
  def updateBook(
    catalog: BookCatalog,
    originalBook: Book,
    updatedTitle: Titles,
    updatedAuthor: Authors,
    updatedISBN: ISBNs,
    updatedDescription: Description,
    updatedCover: CoverImages,
    updatedCategories: Set[Categories]
  ): Try[BookCatalog] = {
    (updatedTitle, updatedAuthor, updatedISBN) match {
      case (repeatedTitle, repeatedAuthor, _)
          if exists(
            remove(
              catalog,
              originalBook.title,
              originalBook.author
            ),
            updatedTitle,
            updatedAuthor
          ) =>
        Failure(
          new InvalidBookException(
            repeatedTitle +
              " by " +
              repeatedAuthor +
              " already exists in the catalog"
          )
        )
      case (_, _, repeatedISBN)
          if exists(
            remove(
              catalog,
              originalBook.title,
              originalBook.author
            ),
            updatedISBN
          ) =>
        Failure(
          new InvalidBookException(
            repeatedISBN +
              " already exists in the catalog"
          )
        )
      case _ =>
        Book.book(
          updatedTitle,
          updatedAuthor,
          updatedISBN,
          updatedDescription,
          updatedCover,
          updatedCategories
        ) match {
          case Valid(updatedBook) =>
            for (action <- catalog.modifySubscribers) {
              action(
                originalBook,
                updatedBook
              )
            }
            Success(
              new BookCatalog(
                (catalog.catalog - originalBook) + updatedBook,
                catalog.addSubscribers,
                catalog.modifySubscribers,
                catalog.deleteSubscribers
              )
            )
          case Invalid(errorReason) =>
            Failure(
              new InvalidBookException(
                errorReason
              )
            )
        }
    }
  }

  /**
    * Remove the book with the given title and author from the catalog
    * @param titleToDelete The title of the book to delete
    * @param authorToDelete The author of the book to delete
    * @return Either the catalog without the given book or an indication thg
    * book cannot be removed
    */
  def deleteBook(
    catalog: BookCatalog,
    titleToDelete: Titles,
    authorToDelete: Authors
  ): Try[BookCatalog] = {
    val deleteResult =
      for {
        bookToDelete <- getByTitleAndAuthor(
          catalog,
          titleToDelete,
          authorToDelete
        )
        resultingCatalog <- Success(
          remove(
            catalog,
            titleToDelete,
            authorToDelete
          )
        )
      } yield (bookToDelete, resultingCatalog)
    deleteResult match {
      case Success((bookToDelete, resultingCatalog)) =>
        catalog.deleteSubscribers.foreach(
          action =>
          action(
            bookToDelete
          )
        )
        Success(
          resultingCatalog
        )
      case Failure(
        failureReason
      ) =>
        Failure(
          failureReason
        )
    }
  }

  /**
    * Register action to perform when book is added to catalog
    * @param catalog Catalog to add subscription to
    * @param addAction Action to perform
    */
  def onAdd(
    catalog: BookCatalog,
    addAction: Book => Unit
  ): BookCatalog =
    new BookCatalog(
      catalog.catalog,
      catalog.addSubscribers :+ addAction,
      catalog.modifySubscribers,
      catalog.deleteSubscribers
    )

  /**
    * Register action to perform when book is added to catalog
    * @param modifyAction Action to perform
    */
  def onModify(
    catalog: BookCatalog,
    modifyAction: (Book, Book) => Unit
  ): BookCatalog =
    new BookCatalog(
      catalog.catalog,
      catalog.addSubscribers,
      catalog.modifySubscribers :+ modifyAction,
      catalog.deleteSubscribers
    )

  def onDelete(
    catalog: BookCatalog,
    deleteAction: Book => Unit
  ): BookCatalog =
    new BookCatalog(
      catalog.catalog,
      catalog.addSubscribers,
      catalog.modifySubscribers,
      catalog.deleteSubscribers :+ deleteAction
    )

  /**
    * Attempt to retrieve book from catalog by ISBN
    * @param catalog Catalog to query about book
    * @param isbn ISBN of book to retrieve
    * @return Either success with book from catalog if book with given ISBN
    *         exists within catalog and Failure otherwise
    */
  def getByISBN(
    catalog: BookCatalog,
    desiredISBN: ISBNs
  ): Try[Book]  = {
    catalog.catalog.find(
      currentBook =>
      currentBook.isbn == desiredISBN
    ) match {
      case Some(correspondingBook) =>
        Success(
          correspondingBook
        )
      case None =>
        Failure(
          new NoBookHasGivenISBN(
            desiredISBN
          )
        )
    }
  }

  /**
    * Attempt to retrieve a book from the catalog by a given title and author
    * @param
    */
  def getByTitleAndAuthor(
    catalog: BookCatalog,
    desiredTitle: Titles,
    desiredAuthor: Authors
  ) : Try[Book] = {
    catalog.catalog.find(
      currentBook =>
      currentBook.title == desiredTitle &&
        currentBook.author == desiredAuthor
    ) match {
      case Some(correspondingBook)=>
        Success(
          correspondingBook
        )
      case None =>
        Failure(
          new NoBookHasGivenTitleAndAuthor(
            desiredTitle,
            desiredAuthor
          )
        )
    }
  }

  /**
    * Determine if book exists within catalog with given title and author
    * @param catalog Catalog being queried
    * @param requestedTitle Title of book being queried
    * @param requestedAuthor Author of book being queried
    * @return True if book exists with given title and author and false
    * otherwise
    */
  def exists(
    catalog: BookCatalog,
    requestedTitle: Titles,
    requestedAuthor: Authors
  ): Boolean = {
    catalog.catalog exists {
      currentBook =>
      currentBook.title == requestedTitle &&
      currentBook.author == requestedAuthor
    }
  }

  /**
    * Determine if book exists within given ISBN
    * @param catalog Catalog being queried
    * @param requestedISBN ISBN of book being searched for
    * @return True if book exists with given ISBN and false otherwise
    */
  def exists(
    catalog: BookCatalog,
    requestedISBN: ISBNs
  ): Boolean = {
    catalog.catalog exists {
      currentBook =>
      currentBook.isbn == requestedISBN
    }
  }

  // Remove a book with the given title and author from the given catalog
  private def remove(
    catalog: BookCatalog,
    existingTitle: Titles,
    existingAuthor: Authors
  ) : BookCatalog =
    new BookCatalog(
      catalog.catalog.filterNot {
        existingBook =>
        existingBook.title == existingTitle &&
        existingBook.author == existingAuthor
      },
      catalog.addSubscribers,
      catalog.modifySubscribers,
      catalog.deleteSubscribers
    )
}
