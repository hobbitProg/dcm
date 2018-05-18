package com.github.hobbitProg.dcm.integrationTests.client.books

import org.scalatest.{FeatureSpec, GivenWhenThen}

class AddCDSpec
    extends FeatureSpec
    with GivenWhenThen {
  feature("The user can add a CD to the CD catalog") {
    info("As someone who wants to keep track of CDs he owns")
    info("I want to add CDs to the CD catalog")
    info ("So that I can know what CDs I own ")

    scenario("A CD with all required fields can be added to the CD catalog") {
      Given("the pre-defined categories")
      And("a populated catalog")
      And("a CD to add to the catalog")
      When ("the information on the CD is entered")
      And("the information is accepted")
      Then("the CD is in the catalog")
      And("the CD is in the repository")
      And("the CD is displayed on the view displaying the CD catalog")
      And("the CDs that were originally on the view displaying the CD "+
        "catalog are still on that window")
      And("no CDs are selected on the window displaying the CD catalog")
      And("the window displaying the information on the selected CD is empty")
    }
  }
}
