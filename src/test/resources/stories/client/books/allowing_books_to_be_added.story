Meta:
@author Kyle Cranmer
@themes Book catalog, adding data

Narrative:
As someone who wants to keep track of books he owns
I want to add books to the catalog catalog
So that I can know what books I own

Scenario: A book with all required fields can be added to the book catalog.
Given the following defined categories:
|category|
|sci-fi|
|conspiracy|
|fantasy|
|thriller|
And the following books that are already in the catalog:
|title|author|isbn|description|cover image|categories|
|Ruins|Kevin J. Anderson|0061052477|Description for Ruins|Ruins.jpg|sci-fi,conspiracy|
|Goblins|Charles Grant|0061054143|Description for Goblins|Goblins.jpg|sci-fi,conspiracy|
And the following book to add to the catalog:
|title|author|isbn|description|cover image|categories|
|Ground Zero|Kevin J. Anderson|006105223X|Description for Ground Zero|GroundZero.jpg|sci-fi,conspiracy|
When I enter this book into the book catalog
And I accept the information on the book
Then the book is in the book catalogs
And the book is displayed on the window displaying the book catalog
And the books that were originally on the window displaying the book catalog are still on that window
And no books are selected on the window displaying the book catalog
And the window displaying the information on the selected book is empty

Scenario: A book that does not have a title cannot be added to the book catalog
Given the following defined categories:
|category|
|sci-fi|
|conspiracy|
|fantasy|
|thriller|
And the following books that are already in the catalog:
|title|author|isbn|description|cover image|categories|
|Ruins|Kevin J. Anderson|0061052477|Description for Ruins|Ruins.jpg|sci-fi,conspiracy|
|Goblins|Charles Grant|0061054143|Description for Goblins|Goblins.jpg|sci-fi,conspiracy|
And the following book to add to the catalog:
|title|author|isbn|description|cover image|categories|
||Kevin J. Anderson|006105223X|Description for Ground Zero|GroundZero.jpg|sci-fi,conspiracy|
When I enter this book into the book catalog
Then I cannot accept the information on the book

Scenario: A book that does not have an author cannot be added to the book catalog
Given the following defined categories:
|category|
|sci-fi|
|conspiracy|
|fantasy|
|thriller|
And the following books that are already in the catalog:
|title|author|isbn|description|cover image|categories|
|Ruins|Kevin J. Anderson|0061052477|Description for Ruins|Ruins.jpg|sci-fi,conspiracy|
|Goblins|Charles Grant|0061054143|Description for Goblins|Goblins.jpg|sci-fi,conspiracy|
And the following book to add to the catalog:
|title|author|isbn|description|cover image|categories|
|Ground Zero||006105223X|Description for Ground Zero|GroundZero.jpg|sci-fi,conspiracy|
When I enter this book into the book catalog
Then I cannot accept the information on the book

Scenario: A book that does not have an ISBN cannot be added to the book catalog
Given the following defined categories:
|category|
|sci-fi|
|conspiracy|
|fantasy|
|thriller|
And the following books that are already in the catalog:
|title|author|isbn|description|cover image|categories|
|Ruins|Kevin J. Anderson|0061052477|Description for Ruins|Ruins.jpg|sci-fi,conspiracy|
|Goblins|Charles Grant|0061054143|Description for Goblins|Goblins.jpg|sci-fi,conspiracy|
And the following book to add to the catalog:
|title|author|isbn|description|cover image|categories|
|Ground Zero|Kevin J. Anderson||Description for Ground Zero|GroundZero.jpg|sci-fi,conspiracy|
When I enter this book into the book catalog
Then I cannot accept the information on the book

Scenario: A book that has a title/author pair that already exists within the book catalog cannot be added to the catalog
Given the following defined categories:
|category|
|sci-fi|
|conspiracy|
|fantasy|
|thriller|
And the following books that are already in the catalog:
|title|author|isbn|description|cover image|categories|
|Ruins|Kevin J. Anderson|0061052477|Description for Ruins|Ruins.jpg|sci-fi,conspiracy|
|Goblins|Charles Grant|0061054143|Description for Goblins|Goblins.jpg|sci-fi,conspiracy|
And the following book to add to the catalog:
|title|author|isbn|description|cover image|categories|
|Ruins|Kevin J. Anderson|006105223X|Description for Ruins|Ruins.jpg|sci-fi,conspiracy|
When I enter this book into the book catalog
Then I cannot accept the information on the book
