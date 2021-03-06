People Simple Preferences
=========================

RESTful API to manage personal info - name & age, and personal preferences - favorite food & color.

The project uses the following technologies: 

* web/REST: Grizzly, Jersey
* marshalling: GSON and JAXB
* persistence: Hibernate, SQLite
* building: Maven
* testing: JUnit, JBehave
* documenting: Swagger.io

## Install

    mvn install

## Details

Implemented date of birth instead of age, because age is dynamic value. But "age" also present in model as transient field - it's read-only & auto-calculated from a date of birth.

Implemented multiple preferences for color & food. Color preferences are sub-set of pre-defined colors represented by `enum`. Food preferences are user-generated.

Added constraint for uniqueness of first name + last name + date of birth.

## Try it!

Run the web app (for dev/test only):

    mvn clean test  # run just once
    mvn exec:java

### JSON or XML

By default, all results will be returned in JSON format. You can manage response media type by implicitly adding `-H 'Accept: application/json'` (that's for `curl`). To get data in XML media format, use `-H 'Accept: application/xml'`.

To submit data, you should always provide `Content-Type` header (there are no defaults for it). To send data in JSON, use `-H 'Content-Type: application/json'`. To send data in XML, use `-H 'Content-Type: application/xml'`.

### People list

Get people list:

    curl -i http://localhost:8080/preferences/people

To see all possible RESTful options for `/preferences/people`, use `-X OPTIONS` (it'll show options in WADL format).

Search the people list:

    curl -i http://localhost:8080/preferences/people/search?lastName=Ivanov

You can search by `firstName` and/or `lastName`.

Create new person:

    curl -i -X POST -H 'Content-Type: application/json' -d '{"firstName": "John", "lastName": "Smith", "dateOfBirth": "1985-12-18", "favoriteColor": ["YELLOW"], "favoriteFood": [{"name": "chocolate"}]}' http://localhost:8080/preferences/people

You can send data in XML format too:

    curl -i -X POST -H 'Content-Type: application/xml' -d '<person><id>1</id><firstName>Petr</firstName><lastName>Petrov</lastName><dateOfBirth>1985-12-18</dateOfBirth><favoriteFood><food><name>chocolate</name></food><food><name>banana</name></food></favoriteFood></person>' http://localhost:8080/preferences/people

If the person _John Smith (1985-12-18)_ already exists, there will be `HTTP 409 Conflict` with error message in the HTTP response body. If the color is _unexpected_ - there will be `HTTP 400 Bad Request` with error message in the body. Otherwise, there will be `HTTP 201 Created` with empty HTTP response body & `Location: link-to-created-person` header.

### Person

Get person details:

    curl -i http://localhost:8080/preferences/people/1

To see all possible RESTful options for `/preferences/people/1`, use `-X OPTIONS`.

Update person details:

    curl -i -X PUT -H 'Content-Type: application/json' -d '{"id":1,"firstName":"John","lastName":"Smith","dateOfBirth":"1988-12-23","favoriteColor":["YELLOW"],"favoriteFood":[{"name":"chocolate"}]}' http://localhost:8080/preferences/people/1

There is the same validation for the person update as for its creation. If you'll try to update _not existing person_ - it will return `HTTP 404 Not Found`.

Delete person:

    curl -X DELETE http://localhost:8080/preferences/people/4

There will be `HTTP 204 No Content` with empty body in case of success.

### Food

As I've mentioned, there is a separate entity for food preferences.

Get food list:

    curl -i http://localhost:8080/preferences/foodlist

To see all possible RESTful options for `/preferences/foodlist`, use `-X OPTIONS`.

Search the food list:

    curl -i http://localhost:8080/preferences/foodlist/search?name=ca

You can search by `name`.

Get food details:

    curl -i http://localhost:8080/preferences/foodlist/1

To see all possible RESTful options for `/preferences/foodlist/1`, use `-X OPTIONS`.

Update food details:

    curl -i -X PUT -H 'Content-Type: application/json' -d '{"name": "apple"}' http://localhost:8080/preferences/foodlist/1

I've not implemented creation and deletion of food record because it's a part of person workflow.

## Dev extras

Special test page created, served by dev server, to test the API in the browser:

    http://localhost:8080/test/web/index.html

First you need to choose HTTP method, then - fill the form & send.

![test_page](simplePeoplePref.png "Test page")

## Tests

JUnit for unit tests and JBehave for application-level tests used. Run tests command:

    mvn clean test integration-test
    
To view the results of JBehave tests go to "target/jbehave/view/reports.html".

## Docs

Swagger.io used to build RESTful API documentation. Build the docs:

    mvn compile

Look for the generated docs in "target/generated-resources/swagger.json".
