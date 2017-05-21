Meta:
@author Kyle Cranmer
@themes Book catalog, modifying data

Narrative:
As someone who enters in information on his books incorrectly
I want to modify the books in the catalog
So the books in the catalog have the correct information

Scenario: The user modifies the title of a book in the book catalog.
Given the following defined categories:
|category|
|sci-fi|
|conspiracy|
|fantasy|
|thriller|
And the following books that are in the catalog:
|title|author|isbn|description|cover image|categories|
|Runs|Kevin J. Anderson|0061052477|Description for Ruins|Ruins.jpg|sci-fi,conspiracy|
|Goblins|Charles Grant|0061054143|Description for Goblins|Goblins.jpg|sci-fi,conspiracy|
When I select the Runs book
And request to modify the book
And change the title from Runs to Ruins
Then the Runs book is no longer displayed on the window displaying the book catalog
And the Ruins book is displayed on the window displaying the book catalog
And no books are selected on the window displaying the book catalog
And the window displaying the information on the selected book is empty