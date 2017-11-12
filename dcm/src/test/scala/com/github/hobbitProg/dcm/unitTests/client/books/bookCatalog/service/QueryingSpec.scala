package com.github.hobbitProg.dcm.unitTests.client.books.bookCatalog.service

import org.scalacheck.{Gen, Arbitrary, Prop}
import Arbitrary.arbitrary
import Gen.const

import cats.data.Validated.{Valid, Invalid}

import org.specs2.ScalaCheck
import org.specs2.matcher.Matcher
import org.specs2.mutable.Specification

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._
import com.github.hobbitProg.dcm.client.books.bookCatalog.service.interpreter.
  BookCatalogServiceInterpreter
import BookCatalogServiceInterpreter._

/**
  * Specification for having the book catalog service determining if a book
  * exists in the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class QueryingSpec
    extends Specification
    with ScalaCheck {
  sequential

  private type BookDataType = (Titles, Authors, ISBNs, Description, CoverImages, Set[Categories])

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

  val catalogGenerator = for {
    catalog <- new BookCatalog
  } yield catalog

  val repositoryGenerator = for {
    repository <- new FakeRepository
  } yield repository

  val dataGenerator = for {
    title <- arbitrary[String].suchThat(_.length > 0)
    author <- arbitrary[String].suchThat(_.length > 0)
    isbn <- arbitrary[String].suchThat(_.length > 0)
    description <- Gen.option(arbitrary[String])
    coverImage <- Gen.oneOf(availableCovers)
    categories <- Gen.listOf(arbitrary[String])
  } yield (title, author, isbn, description, coverImage, categories.toSet)

  "When the book service is queried to see if a book exists with a given " +
  "title and author" >> {
    "indicates a book exists when a book exists in the catalog with the " +
    "given title and author" >> {
      Prop.forAll(catalogGenerator, repositoryGenerator, dataGenerator) {
        (catalog: BookCatalog, repository: FakeRepository, bookData: BookDataType) => {
          bookData match {
            case (title, author, isbn, description, coverImage, categories) =>
              val resultingCatalog =
                insertBook(
                  catalog,
                  title,
                  author,
                  isbn,
                  description,
                  coverImage,
                  categories
                )(
                  repository
                )
              resultingCatalog match {
                case Valid(populatedCatalog) =>
                  bookExists(
                    populatedCatalog,
                    title,
                    author
                  )(
                    repository
                  )
                case Invalid(_) => false
              }
          }
        }
      }
    }

    "indicates a book exists when a book exists in the repository with the " +
    "given title and author" >> pending
    "indicates no book exists when no book exists in the catalog nor the " +
    "repository with the given title and author" >> pending
  }
}
