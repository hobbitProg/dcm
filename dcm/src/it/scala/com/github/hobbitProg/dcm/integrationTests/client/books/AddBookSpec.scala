package com.github.hobbitProg.dcm.integrationTests.client.books

import org.scalatest.{FeatureSpec, GivenWhenThen}

class AddBookSpec
    extends FeatureSpec
    with GivenWhenThen {
  feature("The user can add a book to the book catalog") {
    info("As someone who wants to keep track of books he owns")
    info("I want to add books to the book catalog")
    info("So that I can know what books I own")

    scenario("A book with all required fields can be added to the book " +
      "catalog.") {
      given("the pre-defined categories")
      and("a populated catalog")
      and("a book to add to the catalog")
      when("the information on the book is entered")
      and("the information is accepted")
      then("the book is in the catalog")
      and("the book is in the repository")
      and("the book is displayed on the view displaying the book catalog")
      and("the books that were originally on the view displaying the book " +
        "catalog are still on that window")
      and("no books are selected on the window displaying the book catalog")
      and("the window displaying the information on the selected book is empty")
      pending
    }

    scenario("A book that does not have a title cannot be added to the book " +
      "catalog") {
      given("the pre-defined categories")
      and("a populated catalog")
      and("the information on the book without a title")
      when("the information on the book is entered")
      then("the information on the book cannot be accepted")
      pending
    }

    scenario("A book that does not have an author cannot be added to the " +
      "book catalog") {
      given("the pre-defined categories")
      and("a populated catalog")
      and("the information on the book without an author")
      when("the information on the book is entered")
      then("the information on the book cannot be accepted")
      pending
    }

    scenario("A book that does not have an ISBN cannot be added to the book " +
      "catalog") {
      given("the pre-defined categories")
      and("a populated catalog")
      and("the information on the book without an ISBN")
      when("the information on the book is entered")
      then("the information on the book cannot be accepted")
      pending
    }

    scenario("A book that has a title/author pair that already exists within " +
      "the book catalog cannot be added to the catalog") {
      given("the pre-defined categories")
      and("a populated catalog")
      and("the information on the book with a duplicate title/author pair")
      when("the information on the book is entered")
      then("the information on the book cannot be accepted")
      pending
    }

    scenario("A book that has an ISBN that already exists within the book " +
      "catalog cannot be added to the catalog") {
      given("the pre-defined categories")
      and("a populated catalog")
      and("the information on the book with a duplicate ISBN")
      when("the information on the book is entered")
      then("the information on the book cannot be accepted")
      pending
    }
  }
}
