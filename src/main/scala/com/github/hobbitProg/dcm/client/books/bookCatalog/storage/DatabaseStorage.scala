package com.github.hobbitProg.dcm.client.books.bookCatalog.storage

import doobie.imports._

import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import com.github.hobbitProg.dcm.client.books.{Categories, ISBNs}
import com.github.hobbitProg.dcm.client.books.bookCatalog.Book

import scala.collection.Set

 /**
  * Database for book catalog storage
  * @author Kyle Cranmer
  * @since 0.1
  */

class DatabaseStorage(
  private val catalogConnection: Transactor[Task]
) extends Storage {
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
 }

object DatabaseStorage {
  private val bookCatalogInsertPrefix: String = "INSERT INTO bookCatalog(Title,Author,ISBN,Description,Cover)VALUES"
}
