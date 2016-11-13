package com.github.hobbitProg.dcm.acceptanceTests.stories.client.books

import org.jbehave.core.annotations._
import org.jbehave.core.model.ExamplesTable

/**
  * Performs steps in stories related to book catalog client
  * @author Kyle Cranmer
  * @since 0.1
  */
class BookCatalogClientSteps {
  @Given("the following defined categories: <existingCategories>")
  @Pending
  def definedCategories(
    @Named("existingCategories")
    existingCategories: ExamplesTable
  ): Unit = {
  }
  @Given("the following books that are already in the catalog: <preExistingBooks>")
  @Pending
  def catalogContents(
    @Named("preExistingBooks")
    preExistingBooks: ExamplesTable
  ): Unit = {
  }

  @Given("the following book to add to the catalog: <newBook>")
  @Pending
  def bookToAdd(
    @Named("newBook")
    newBook: ExamplesTable
  ): Unit = {
  }

  @When("I enter this books into the book catalog")
  @Pending
  def addBooksToCatalog = {
  }

  @Then("the book is in the book catalogs")
  @Pending
  def bookExistsInCatalog = {
  }

  @Then("the book is displayed on the window displaying the book catalog")
  @Pending
  def newBookIsDisplayedWithinBookCatalog = {
  }

  @Then("the books that were originally on the window displaying the book catalog are still on that window")
  @Pending
  def originalBooksAreStillDisplayed = {
  }

  @Then("no books are selected on the window displaying the book catalog")
  @Pending
  def noBooksAreSelectedInBookCatalogWindow = {
  }

  @Then("the window displaying the information on the selected book is empty")
  @Pending
  def noSelectedBookIsDisplayed = {
  }
}
