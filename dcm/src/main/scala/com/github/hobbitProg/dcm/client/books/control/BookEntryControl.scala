package com.github.hobbitProg.dcm.client.books.control

import java.net.URI

import scalafx.collections.ObservableBuffer
import scalafx.stage.{Stage, Window}

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  BookCatalogRepository
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.
  BookCatalogService
import com.github.hobbitProg.dcm.client.books.control.model.BookModel
import com.github.hobbitProg.dcm.client.books.gui.linuxDesktop.view.image.
  CoverImage
import com.github.hobbitProg.dcm.client.dialog.{CategorySelectionDialog,
  ImageChooser}

/**
  * Controller for view that enters information on a book
  * @author Kyle Cranmer
  * @since 0.2
  */
abstract class BookEntryControl(
  private val catalog: BookCatalog,
  private val coverImageChooser: ImageChooser,
  private val bookCatalogService: BookCatalogService[BookCatalog]
) {
  import bookCatalogService._

  /**
    * Save modified title
    * @param modifiedTitle Title user changed
    */
  def updateTitle(
    modifiedTitle: Titles
  ) = {
    bookBeingEdited.title =
      modifiedTitle
  }

  /**
    * Save modified author
    * @param modifiedAuthor Author user changed
    */
  def updateAuthor(
    modifiedAuthor: Authors
  ) = {
    bookBeingEdited.author =
      modifiedAuthor
  }

  /**
    * Save modified ISBN
    * @param modifiedISBN ISBN user changed
    */
  def updateISBN(
    modifiedISBN: ISBNs
  ) = {
    bookBeingEdited.isbn =
      modifiedISBN
  }

  /**
    * Save modified description
    * @param descriptionText Description user changed
    */
  def updateDescription(
    descriptionText: String
  ) = {
    bookBeingEdited.description =
      Some(
        descriptionText
      )
  }

  /**
    * Save modified cover image
    * @param parentWindow Window containing book entry view
    * @param coverImageControl Control that displays cover
    */
  def updateCoverImage(
    parentWindow: Window,
    coverImageControl: CoverImage
  ) = {
    val coverImageFile =
      coverImageChooser.selectImage(
        parentWindow
      )
    bookBeingEdited.coverImage =
      Some[URI](
        coverImageFile.toURI
      )
    coverImageControl.image =
      coverImageFile.toURI
  }

  /**
    * Select categories to associate with book being edited
    * @param unassociatedCategories Categories not associated with book
    * @param associatedCategories CategoriesAssociatedWithBook
    */
  def selectCategories(
    unassociatedCategories: ObservableBuffer[Categories],
    associatedCategories: ObservableBuffer[Categories]
  ) = {
    new Stage {
      outer =>
      title = "Associate categories with book"
      scene =
        new CategorySelectionDialog(
          unassociatedCategories,
          associatedCategories
        )
    }.showAndWait
    bookBeingEdited.categories =
      associatedCategories.toSet[String]
  }

  /**
    * Save modifications to book
    * @param repository Repository containing book catalog information
    * @param parent Stage containing control
    */
  def saveBook(
    repository: BookCatalogRepository,
    parent: javafx.stage.Stage
  )

  /**
    * Determine if book containing given title and author exists in
    * book catalog
    * @param title Title of book being examined
    * @param author Author of book being examined
    * @param repository Repository containing book catalog informaiton
    * @return True if book with given title and author exists in
    * catalog and false otherwise
    */
  def existsInCatalog(
    title: Titles,
    author: Authors,
    repository: BookCatalogRepository
  ) : Boolean = {
    bookExists(
      catalog,
      title,
      author
    )(
      repository
    )
  }

  /**
    * Determine if book containing given isbn exists in book catalog
    * @param isbn ISBN of book being examined
    * @param repository Repository containing book catalog informaiton
    * @return True if book with given isbn exists in catalog and false
    * otherwise
    */
  def existsInCatalog(
    isbn: ISBNs,
    repository: BookCatalogRepository
  ): Boolean = {
    bookExists(
      catalog,
      isbn
    )(
      repository
    )
  }

  // Model of book being worked on
  protected val bookBeingEdited: BookModel =
    new BookModel()
}
