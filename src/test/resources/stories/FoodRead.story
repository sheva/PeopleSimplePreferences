Story: RESTful service should support GET requests to view complete list of food.

Scenario: GET request to food resource in XML|JSON media format and status 200 OK.

Given GET request to 'foodlist' resource with <mediaType>.
When there are 7 food records in the database.
Then response contains food collection in format <mediaType> and status code 200.

Examples:
|mediaType|
|application/xml|
|application/json|

Scenario: Search by name or category requests in XML|JSON media format and status 200 OK.

Given request on 'foodlist' resource with query params name=<name> with <mediaType>.
When there are <amount> food records in the database with name=<name>.
Then response contains food collection of elements in <mediaType> media format and status code 200.

Examples:
|name|amount|mediaType|
|candies|1|application/xml|
|c|3|application/xml|
| |7|application/xml|
|null|7|application/xml|
|test|0|application/xml|
|candies|1|application/json|
|c|3|application/json|
| |7|application/json|
|null|7|application/json|
|test|0|application/json|
