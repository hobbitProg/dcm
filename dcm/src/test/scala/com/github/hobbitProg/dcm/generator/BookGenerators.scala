package com.github.hobbitProg.dcm.generator

import scala.collection.Set

import org.scalacheck.{Gen, Arbitrary}
import Arbitrary.arbitrary
import Gen.const

import com.github.hobbitProg.dcm.client.books.bookCatalog.model._

/**
  * Common generators for books
  * @author Kyle Cranmer
  * @since 0.2
  */
trait BookGenerators {

  protected type BookInfoType = (
    Titles,
    Authors,
    ISBNs,
    Description,
    CoverImages,
    Set[Categories]
  )

  protected val availableCovers =
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

  protected val TitleGen =
    arbitrary[Titles].suchThat(_.length > 0)

  protected val AuthorGen =
    arbitrary[Authors].suchThat(_.length > 0)

  protected val ISBNGen =
    for {
      digitOne <- Gen.chooseNum(0,9)
      digitTwo <- Gen.chooseNum(0,9)
      digitThree <- Gen.chooseNum(0,9)
      digitFour <- Gen.chooseNum(0,9)
      digitFive <- Gen.chooseNum(0,9)
      digitSix <- Gen.chooseNum(0,9)
      digitSeven <- Gen.chooseNum(0,9)
      digitEight <- Gen.chooseNum(0,9)
      digitNine <- Gen.chooseNum(0,9)
      digitTen <- Gen.chooseNum(0,9)
      digitEleven <- Gen.chooseNum(0,9)
      digitTwelve <- Gen.chooseNum(0,9)
      digitThirteen <- Gen.chooseNum(0,9)
    } yield digitOne.toString +
  digitTwo.toString +
  digitThree.toString +
  digitFour.toString +
  digitFive.toString +
  digitSix.toString +
  digitSeven.toString +
  digitEight.toString +
  digitNine.toString +
  digitTen.toString +
  digitEleven.toString +
  digitTwelve.toString +
  digitThirteen.toString

  protected val DescriptionGen =
    Gen.option(arbitrary[String])

  protected val CoverImageGen =
    Gen.oneOf(availableCovers)

  protected val CategoriesGen =
    for {
      categories <- Gen.listOf(arbitrary[Categories])
    } yield categories.toSet

  protected val bookDataGen = for {
    title <- TitleGen
    author <- AuthorGen
    isbn <- ISBNGen
    description <- DescriptionGen
    coverImage <- CoverImageGen
    categories <- CategoriesGen
  } yield(
    title,
    author,
    isbn,
    description,
    coverImage,
    categories.toSet
  )
}
