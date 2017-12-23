package com.github.hobbitProg.dcm.integrationTests.client.books

import org.scalatest.{FeatureSpec, GivenWhenThen, BeforeAndAfter, Matchers}

/**
  * Specification for modifying a book in the catalog
  * @author Kyle Cranmer
  * @since 0.2
  */
class ModifyBookSpec
    extends FeatureSpec
    with GivenWhenThen {
  Feature("The user can modify a book within the book catalog") {
    info("As someone who wants to keep track of books he owns")
    info("I want to change information on books within the book catalog")
    info("So any problems with the books can be fixed")

    Scenario("A book within the book catalog can have its title changed") {
      Given("the pre-defined categories")
      And("a populated catalog")
      And("the title of the book to modify")
      And("the new title of the book")
      When("the book to modify is selected")
      And("the book is to be modified")
      And("the title of the book is changed")
      And("the information on the book is accepted")
      Then("the updated book is in the catalog")
      And("the updated book is in the repository")
      And("the original book is not in the catalog")
      And("the original book is not in the repository")
      And("the updated book is displayed on the view displaying the book " +
        "catalog")
      And("the original book is not displayed on the view displaying the " +
        "book catalog")
      And("no books are selected on the window displaying the book catalog")
      And("the window displaying the information on the selected book is empty")
    }
  }

}
