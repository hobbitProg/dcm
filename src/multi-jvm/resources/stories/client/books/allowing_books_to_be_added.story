Meta:
@author Kyle Cranmer
@themes Book catalog, adding data

Narrative:
As someone who wants to keep track of books he owns
I want to add books to the catalog catalog
So that I can know what books I own

Scenario:
Given the following defined categories:
|category|
|sci-fi|
|conspiracy|
|fantasy|
|thriller|
And the following books that are already in the catalog:
|title|author|isbn|description|cover image}categores|
|Ruins|Kevin J. Anderson|0061052477|Description for Ruins|Ruins.jpg|sci-fi,conspiracy|
|Goblins|Charles Grant|0061054143|Description for Goblins|Goblins.jpg|sci-fi,conspiracy|
And the following book to add to the catalog:
|title|author|isbn|description|cover image|categories|
|Ground Zero|Kevin J. Anderson|006105223X|Description for Ground Zero|GrouondZero.jpg|sci-fi,conspiracy|
When I enter this book into the book catalog
Then the book is in the book catalogs
And the book is displayed on the window displaying the book catalog
And the books that were originally on the window displaying the book catalog are still on that window
And no books are selected on the window displaying the book catalog
And the window displaying the information on the selected book is empty