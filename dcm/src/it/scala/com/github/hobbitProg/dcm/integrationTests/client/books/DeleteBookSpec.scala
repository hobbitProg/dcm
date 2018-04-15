package com.github.hobbitProg.dcm.integrationTests.client.books

import org.scalatest.{FeatureSpec, GivenWhenThen}

class DeleteBookSpec
    extends FeatureSpec
    with GivenWhenThen {
  feature("The user can remove a book from the book catalog") {
    info("As someone who wants to keep trak of books he owns")
    info("I want to remove books from the book catalog")
    info ("So that I can know what books I own")

    scenario("A book within the book catalog can be removed") {
      Given("the pre-defined categories")
      And("a populated catalog")
      And("the title of the book to delete")
      When("the book to delete is selected")
      And("the book is deleted")
      Then("the book is not in the catalog")
      And("the book is not in the repository")
      (pending)
    }
    scenario("The delete button is inactive when no books are selected"){
      Given("the pre-defined catgories")
      And("a populated catalog")
      When("no books are selected")
      Then("the delete button is inactive")
      (pending)
    }
  }
}
