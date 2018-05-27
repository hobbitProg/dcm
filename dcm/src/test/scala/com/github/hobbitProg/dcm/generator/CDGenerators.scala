package com.github.hobbitProg.dcm.generator

import scala.collection.Set

import org.scalacheck.{Gen, Arbitrary}
import Arbitrary.{arbitrary, arbChar}
import Gen.const

import com.github.hobbitProg.dcm.client.cds.cdCatalog.model._

/**
  * Common generators for CDs
  * @author Kyle Cranmer
  * @since 0.4
  */
trait CDGenerators {
  protected type CDDataType = (
    Titles,
    Artists,
    ISRCs,
    CoverImages,
    Set[Categories]
  )

  protected case class TestCD(
    val title: Titles,
    val artist: Artists,
    val isrc: ISRCs,
    val cover: CoverImages,
    val categories: Set[Categories]
  ) extends CD{
  };


  protected val availableCovers =
    Seq(
      "/aerosmith_draw_the_line.jpg",
      "/aerosmith_permanent_vacation.jpg",
      "/aerosmith_pump.jpg",
      "/aerosmith_toys_in_the_attic.jpg"
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
    arbitrary[Titles].suchThat(
      _.length > 0
    )

  protected val ArtistGen =
    arbitrary[Artists].suchThat(
      _.length > 0
    )

  protected val ISRCGen =
    for {
      countryCodeOne <- arbChar.arbitrary
      countryCodeTwo <- arbChar.arbitrary
      registrantCodeOne <- Gen.chooseNum(0, 9)
      registrantCodeTwo <- Gen.chooseNum(0, 9)
      registrantCodeThree <- Gen.chooseNum(0, 9)
      yearOne <- Gen.chooseNum(0, 9)
      yearTwo <- Gen.chooseNum(0, 9)
      identifierOne <- Gen.chooseNum(0, 9)
      identifierTwo <- Gen.chooseNum(0, 9)
      identifierThree <- Gen.chooseNum(0, 9)
      identifierFour <- Gen.chooseNum(0, 9)
      identifierFive <- Gen.chooseNum(0, 9)
    } yield countryCodeOne.toString +
  countryCodeTwo.toString +
  registrantCodeOne.toString +
  registrantCodeTwo.toString +
  registrantCodeThree.toString +
  yearOne.toString +
  yearTwo.toString +
  identifierOne.toString +
  identifierTwo.toString +
  identifierThree.toString +
  identifierFour.toString +
  identifierFive.toString

  protected val CoverImageGen =
    Gen.oneOf(
      availableCovers
    )

  protected val CategoriesGen =
    for {
      categories <- Gen.listOf(
        arbitrary[Categories]
      )
    } yield categories.toSet

  protected val CDDataGen = for {
    title <- TitleGen
    artist <- ArtistGen
    isrc <- ISRCGen
    cover <- CoverImageGen
    categories <- CategoriesGen
  } yield (
    title,
    artist,
    isrc,
    cover,
    categories
  )

  protected val CatalogGenerator = for {
    catalog <- new CDCatalog
  } yield catalog
}
