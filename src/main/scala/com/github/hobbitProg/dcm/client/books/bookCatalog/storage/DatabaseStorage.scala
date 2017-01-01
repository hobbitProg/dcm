package com.github.hobbitProg.dcm.client.books.bookCatalog.storage

import doobie.imports._

import java.net.URI

import scalaz._
import Scalaz._
import scalaz.concurrent.Task

import scala.collection.Set

import com.github.hobbitProg.dcm.client.books._
import com.github.hobbitProg.dcm.client.books.bookCatalog.Book

/**
  * Database for book catalog storage
  * @author Kyle Cranmer
  * @since 0.1
  */
class DatabaseStorage(
  private val catalogConnection: Transactor[Task]
) extends Storage {
  private type BookType = (Titles, Authors, ISBNs, String, String)
  private type CategoryMappingType = (ISBNs,Categories)

  /**
    * Save book into storage
    *
    * @param bookToSave Book to place into storage
    */
  override def save(
    bookToSave: Book
  ): Unit = {
    val descriptionSQL =
      bookToSave.description match {
        case Some(descriptionValue) => descriptionValue
        case None => "NULL"
      }
    val coverImageSQL =
      bookToSave.coverImage match {
        case Some(bookCover) => bookCover.toString
        case None => "NULL"
      }
    val dataToInsert: String =
      "'${bookToSave.title}','${bookToSave.author}','${bookToSave.isbn}','$descriptionSQL','$coverImageSQL'"
    val categoriesToInsert =
      bookToSave.categories.map(
        category =>
          new CategoryMappingType(
            bookToSave.isbn,
            category
          )
      ).toList
    val mainBookStatement =
      sql"INSERT INTO bookCatalog(Title,Author,ISBN,Description,Cover)VALUES(${bookToSave.title},${bookToSave.author},${bookToSave.isbn},$descriptionSQL,$coverImageSQL);"
    val statementToInsertCategories =
      Update[CategoryMappingType](
        "INSERT INTO categoryMapping(ISBN,Category)VALUES(?,?);"
      )
    val insertStatement =
      for {
        mainBookInsert <- mainBookStatement.update.run
        categoryUpdate <-
        statementToInsertCategories.updateMany(
          categoriesToInsert
        )
      } yield mainBookInsert + categoryUpdate
    val insertedBook =
      insertStatement.transact(
        catalogConnection
      ).unsafePerformSync
  }

   /**
     * Categories that can be associated with books
     *
     * @return Categories that can be associated with books
     */
   override def definedCategories: Set[Categories] = {
     sql"SELECT Category FROM definedCategories;"
       .query[Categories]
       .vector
       .transact(
         catalogConnection
       ).unsafePerformSync
       .toSet
   }

   /**
     * Books that exist in storage
     * @return Books that exist in storage
     */
   override def contents: Set[Book] = {
     val rawBookData: Set[BookType] =
       sql"SELECT Title,Author,ISBN,Description,Cover FROM bookCatalog;"
         .query[BookType]
         .vector
         .transact(
           catalogConnection
         ).unsafePerformSync
         .toSet
     var collectedBooks: Set[Book] =
       Set[Book]()
     for (
       rawBook <- rawBookData
     ) {
       val associatedCategories =
         sql"SELECT Category FROM catgoryMapping WHERE ISBN=${rawBook._3};"
         .query[Categories]
         .vector
         .transact(
           catalogConnection
         ).unsafePerformSync
         .toSet
       val description =
         rawBook._4 match {
           case "NULL" => None
           case actualDescription => Some(actualDescription)
         }
       val coverImage =
         rawBook._5 match {
           case "NULL" => None
           case actualCoverImage => Some(new URI(actualCoverImage))
         }
       collectedBooks =
         collectedBooks +
           new Book(
             rawBook._1,
             rawBook._2,
             rawBook._3,
             description,
             coverImage,
             associatedCategories
           )
     }
     collectedBooks
   }
 }

object DatabaseStorage {
  private val bookCatalogInsertPrefix: String = "INSERT INTO bookCatalog(Title,Author,ISBN,Description,Cover)VALUES"
}
