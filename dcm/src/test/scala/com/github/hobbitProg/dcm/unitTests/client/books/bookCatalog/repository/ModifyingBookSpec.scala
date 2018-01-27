package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.repository

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Prop, Gen}
import Gen.const

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

import scala.collection.Set

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.repository.
  interpreter.BookCatalogRepositoryInterpreter

/**
  * Verifies modifying books within book catalog repository
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyingBookSpec
    extends Specification
    with ScalaCheck {
  sequential

  private type BookDataTypeWithNewTitle =
    (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories], Titles)
  private type BookDataTypeWithNewAuthor =
    (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories], Authors)
  private type BookDataTypeWithNewISBN =
    (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories], ISBNs)
  private type BookDataTypeWithNewDescription =
    (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories], Description)
  private type BookDataTypeWithNewCover =
    (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories], CoverImages)
  private type BookDataTypeWithNewCategories =
    (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories], Set[Categories])

  private case class TestBook(
    title: Titles,
    author: Authors,
    isbn: ISBNs,
    description: Description,
    coverImage: CoverImages,
    categories: Set[Categories]
  ) extends Book {
  }

  private val availableCovers =
    Seq(
      "/Goblins.jpg",
      "/GroundZero.jpg",
      "/Ruins.jpg"
    ).map(
      image =>
      Some(
        getClass().
          getResource(
            image
          ).toURI
      )
    )

  val databaseGenerator = for {
    database <- new StubDatabase()
  } yield database

  val repositoryGenerator = for {
    repository <- new BookCatalogRepositoryInterpreter
  } yield repository

  val newTitleDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
    newTitle <- arbitrary[String].suchThat(generatedTitle => generatedTitle != title && generatedTitle.length > 0)
  } yield (title, author, isbn, description, coverImage, categories.toSet, newTitle)

  val emptyTitleDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield (title, author, isbn, description, coverImage, categories.toSet, "")

  val newAuthorDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
    newAuthor <- arbitrary[String].suchThat(generatedAuthor => generatedAuthor != author && generatedAuthor.length > 0)
  } yield (title, author, isbn, description, coverImage, categories.toSet, newAuthor)

  val newISBNDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
    newISBN <- arbitrary[String].suchThat(generatedISBN => generatedISBN != isbn && generatedISBN.length > 0)
  } yield (title, author, isbn, description, coverImage, categories.toSet, newISBN)

  val newDescriptionDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
    newDescription <- Gen.option(arbitrary[String]).suchThat(generatedDescription => generatedDescription != description)
  } yield (title, author, isbn, description, coverImage, categories.toSet, newDescription)

  val newCoverImageDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
    newCover <- Gen.oneOf(availableCovers).suchThat(generatedCover => generatedCover != coverImage)
  } yield (title, author, isbn, description, coverImage, categories.toSet, newCover)

  val newCategoriesDataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
    newCategories <- Gen.listOf(arbitrary[String]).suchThat(generatedCategories => generatedCategories != categories)
  } yield (title, author, isbn, description, coverImage, categories.toSet, newCategories.toSet)

  // Modify the title of a book in the repository
  private def modifyTitleOfBook(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataTypeWithNewTitle
  ) : Either[String, Book] =
    bookData match {
      case (title, author, isbn, description, coverImage, categories, newTitle) =>
        val originalBook =
          TestBook(
            title,
            author,
            isbn,
            description,
            coverImage,
            categories
          )
        val modifiedBook =
          TestBook(
            newTitle,
            author,
            isbn,
            description,
            coverImage,
            categories
          )
        repository.setConnection(
          database.connectionTransactor
        )
        repository.update(
          originalBook,
          modifiedBook
        )
    }

  // Modify the author of a book in the repository
  private def modifyAuthorOfBook(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataTypeWithNewAuthor
  ) : Either[String, Book] =
    bookData match {
      case (title, author, isbn, description, coverImage, categories, newAuthor) =>
        val originalBook =
          TestBook(
            title,
            author,
            isbn,
            description,
            coverImage,
            categories
          )
        val modifiedBook =
          TestBook(
            title,
            newAuthor,
            isbn,
            description,
            coverImage,
            categories
          )
        repository.setConnection(
          database.connectionTransactor
        )
        repository.update(
          originalBook,
          modifiedBook
        )
    }

  // Modify the ISBN of a book in the repository
  private def modifyISBNOfBook(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataTypeWithNewISBN
  ) =
    bookData match {
      case (title, author, isbn, description, coverImage, categories, newISBN) =>
        val originalBook =
          TestBook(
            title,
            author,
            isbn,
            description,
            coverImage,
            categories
          )
        val modifiedBook =
          TestBook(
            title,
            author,
            newISBN,
            description,
            coverImage,
            categories
          )
        repository.setConnection(
          database.connectionTransactor
        )
        repository.update(
          originalBook,
          modifiedBook
        )
    }

  // Modify the description of a book in the repository
  private def modifyDescriptionOfBook(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataTypeWithNewDescription
  ) : Either[String, Book] =
    bookData match {
      case (title, author, isbn, description, coverImage, categories, newDescription) =>
        val originalBook =
          TestBook(
            title,
            author,
            isbn,
            description,
            coverImage,
            categories
          )
        val modifiedBook =
          TestBook(
            title,
            author,
            isbn,
            newDescription,
            coverImage,
            categories
          )
        repository.setConnection(
          database.connectionTransactor
        )
        repository.update(
          originalBook,
          modifiedBook
        )
    }

  // Modify the cover of the book
  private def modifyCoverOfBook(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataTypeWithNewCover
  ) : Either[String, Book] =
  bookData match {
    case (title, author, isbn, description, coverImage, categories, newCover) =>
      val originalBook =
        TestBook(
          title,
          author,
          isbn,
          description,
          coverImage,
          categories
        )
      val modifiedBook =
        TestBook(
          title,
          author,
          isbn,
          description,
          newCover,
          categories
        )
      repository.setConnection(
        database.connectionTransactor
      )
      repository.update(
        originalBook,
        modifiedBook
      )
  }

  // Modify the categories of a book in the repository
  private def modifyCategoriesOfBook(
    database: StubDatabase,
    repository: BookCatalogRepositoryInterpreter,
    bookData: BookDataTypeWithNewCategories
  ) : Either[String, Book] =
    bookData match {
      case (title, author, isbn, description, coverImage, categories, newCategories) =>
        val originalBook =
          TestBook(
            title,
            author,
            isbn,
            description,
            coverImage,
            categories
          )
        val modifiedBook =
          TestBook(
            title,
            author,
            isbn,
            description,
            coverImage,
            newCategories
          )
        repository.setConnection(
          database.connectionTransactor
        )
        repository.update(
          originalBook,
          modifiedBook
        )
    }

  "Modifying the title of an existing book within the repository" >> {
    "the repository is updated" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, newTitleDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewTitle) => {
          modifyTitleOfBook(
            database,
            repository,
            bookData
          ) must beRight
        }
      }
    }

    "the updated book is placed into the repository" >> {
      Prop.forAll(databaseGenerator, repositoryGenerator, newTitleDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewTitle) => {
          bookData match {
            case (_, author, isbn, description, coverImage, categories, newTitle) =>
              modifyTitleOfBook(
                database,
                repository,
                bookData
              )
              TestBook(
                database.addedTitle,
                database.addedAuthor,
                database.addedISBN,
                database.addedDescription,
                database.addedCover,
                database.addedCategoryAssociations.filter {
                  association =>
                  association._1 == isbn
                }.map {
                  association =>
                  association._2
                }
              ) should beEqualTo(
                TestBook(
                  newTitle,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              )
          }
        }
      }
    }

    "the original book is no longer in the repository" >> {
      Prop.forAll(databaseGenerator, repositoryGenerator, newTitleDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewTitle) => {
          bookData match {
            case (_, _, isbn, _, _, _, _) =>
              modifyTitleOfBook(
                database,
                repository,
                bookData
              )
              database.removedISBN must beEqualTo(isbn)
          }
        }
      }
    }
  }

  "Modifying the author of an existing book within the repository" >> {
    "the repository is updated" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, newAuthorDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewAuthor) => {
          modifyAuthorOfBook(
            database,
            repository,
            bookData
          ) must beRight
        }
      }
    }

    "the updated book is placed into the repository" >> {
      Prop.forAll(databaseGenerator, repositoryGenerator, newAuthorDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewAuthor) => {
          bookData match {
            case (title, _, isbn, description, coverImage, categories, newAuthor) =>
              modifyAuthorOfBook(
                database,
                repository,
                bookData
              )
              TestBook(
                database.addedTitle,
                database.addedAuthor,
                database.addedISBN,
                database.addedDescription,
                database.addedCover,
                database.addedCategoryAssociations.filter {
                  association =>
                  association._1 == isbn
                }.map {
                  association =>
                  association._2
                }
              ) should beEqualTo(
                TestBook(
                  title,
                  newAuthor,
                  isbn,
                  description,
                  coverImage,
                  categories
                )
              )
          }
        }
      }
    }

    "the original book is no longer in the repository" >> {
      Prop.forAll(databaseGenerator, repositoryGenerator, newAuthorDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewAuthor) => {
          bookData match {
            case (_, _, isbn, _, _, _, _) =>
              modifyTitleOfBook(
                database,
                repository,
                bookData
              )
              database.removedISBN must beEqualTo(isbn)
          }
        }
      }
    }
  }

  "Modifying the ISBN of an existing book within the repository" >>{
    "the repository is updated" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, newISBNDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewISBN) => {
          modifyISBNOfBook(
            database,
            repository,
            bookData
          ) must beRight
        }
      }
    }

    "the updated book is placed into the repository" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, newISBNDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewISBN) => {
          bookData match {
            case (title, author, _, description, coverImage, categories, newISBN) =>
              modifyISBNOfBook(
                database,
                repository,
                bookData
              )
              TestBook(
                database.addedTitle,
                database.addedAuthor,
                database.addedISBN,
                database.addedDescription,
                database.addedCover,
                database.addedCategoryAssociations.filter {
                  association =>
                  association._1 == newISBN
                }.map {
                  association =>
                  association._2
                }
              ) should beEqualTo(
                TestBook(
                  title,
                  author,
                  newISBN,
                  description,
                  coverImage,
                  categories
                )
              )
          }
        }
      }
    }

    "the original book is no longer in the repository" >> {
      Prop.forAll(databaseGenerator, repositoryGenerator, newISBNDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewISBN) => {
          bookData match {
            case (_, _, isbn, _, _, _, _) =>
              modifyISBNOfBook(
                database,
                repository,
                bookData
              )
              database.removedISBN must beEqualTo(isbn)
          }
        }
      }
    }
  }

  "Modifying the description of an existing book within the repository" >> {
    "the repository is updated" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, newDescriptionDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewDescription) => {
          modifyDescriptionOfBook(
            database,
            repository,
            bookData
          ) must beRight
        }
      }
    }

    "the updated book is placed into the repository" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, newDescriptionDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewDescription) => {
          bookData match {
            case (title, author, isbn, _, coverImage, categories, newDescription) =>
              modifyDescriptionOfBook(
                database,
                repository,
                bookData
              )
              TestBook(
                database.addedTitle,
                database.addedAuthor,
                database.addedISBN,
                database.addedDescription,
                database.addedCover,
                database.addedCategoryAssociations.filter {
                  association =>
                  association._1 == isbn
                }.map {
                  association =>
                  association._2
                }
              ) should beEqualTo(
                TestBook(
                  title,
                  author,
                  isbn,
                  newDescription,
                  coverImage,
                  categories
                )
              )
          }
        }
      }
    }
  }

  "Modifying the cover of an existing book within the repository" >> {
    "the repository is updated" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, newCoverImageDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewCover) => {
          modifyCoverOfBook(
            database,
            repository,
            bookData
          ) must beRight
        }
      }
    }

    "the updated book is placed into the repository" >> {
      Prop.forAll(databaseGenerator, repositoryGenerator, newCoverImageDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewCover) => {
          bookData match {
            case (title, author, isbn, description, _, categories, newCover) =>
              modifyCoverOfBook(
                database,
                repository,
                bookData
              )
              TestBook(
                database.addedTitle,
                database.addedAuthor,
                database.addedISBN,
                database.addedDescription,
                database.addedCover,
                database.addedCategoryAssociations.filter {
                  association =>
                  association._1 == isbn
                }.map {
                  association =>
                  association._2
                }
              ) should beEqualTo(
                TestBook(
                  title,
                  author,
                  isbn,
                  description,
                  newCover,
                  categories
                )
              )
          }
        }
      }
    }
  }

  "Modifying the categories associated with a book within the repository" >> {
    "the repository is updated" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, newCategoriesDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewCategories) => {
          modifyCategoriesOfBook(
            database,
            repository,
            bookData
          ) must beRight
        }
      }
    }

    "the updated book is placed into the repository" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, newCategoriesDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewCategories) => {
          bookData match {
            case (title, author, isbn, description, coverImage, _, newCategories) =>
              modifyCategoriesOfBook(
                database,
                repository,
                bookData
              )
              TestBook(
                database.addedTitle,
                database.addedAuthor,
                database.addedISBN,
                database.addedDescription,
                database.addedCover,
                database.addedCategoryAssociations.filter {
                  association =>
                  association._1 == isbn
                }.map {
                  association =>
                  association._2
                }
              ) should beEqualTo(
                TestBook(
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  newCategories
                )
              )
          }
        }
      }
    }
  }

  "Removing the title of a book within the repository" >> {
    "the repository is not updated" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, emptyTitleDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewTitle) => {
          modifyTitleOfBook(
            database,
            repository,
            bookData
          ) must beLeft
        }
      }
    }

    "the original book is still in the repository" >> {
      Prop.forAllNoShrink(databaseGenerator, repositoryGenerator, emptyTitleDataGenerator) {
        (database: StubDatabase, repository: BookCatalogRepositoryInterpreter, bookData: BookDataTypeWithNewTitle) => {
          database.addedTitle = ""
          database.addedAuthor = ""
          database.addedISBN = ""
          database.addedDescription = None
          database.addedCover = None
          database.addedCategoryAssociations =
            Set[(ISBNs, Categories)]()
          bookData match {
            case (_, _, isbn, _, _, _, _) =>
              modifyTitleOfBook(
                database,
                repository,
                bookData
              )
              TestBook(
                database.addedTitle,
                database.addedAuthor,
                database.addedISBN,
                database.addedDescription,
                database.addedCover,
                database.addedCategoryAssociations.filter {
                  association =>
                  association._1 == isbn
                }.map {
                  association =>
                  association._2
                }
              ) should beEqualTo(
                TestBook(
                  "",
                  "",
                  "",
                  None,
                  None,
                  Set[Categories]()
                )
              )
          }
        }
      }
    }
  }
}
