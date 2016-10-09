Story: RESTful service should support GET requests to view complete list of people registered.

Scenario: GET request to 'people' resource returns response in XML|JSON with status 200 OK.

Given GET request to resource 'people'. Request supported <mediaType>.
When there is 3 people in the database.
Then response contains people collection with 200 and <mediaType>.

Examples:
|mediaType|
|application/xml|
|application/json|

Scenario: Search by firstName or lastName requests in XML|JSON media format and status 200 OK.

Given search?firstName=<firstName>&lastName=<lastName> request on 'people' resource with <mediaType>.
When there are <amount> person records in the database with firstName=<firstName> and lastName=<lastName> pairs.
Then response contains people collection with <amount> of elements with firstName=<firstName> and lastName=<lastName> in <mediaType> media format and status code 200.

Examples:
|firstName|lastName|amount|mediaType|
|vas|Ivanov|1|application/xml|
|vas|Ivanov|1|application/json|
| |iva|2|application/json|
|null| |3|application/json|
|null|null|3|application/json|
||null|3|application/json|
|some|test|0|application/json|
|vas|Ivanov|1|application/xml|
| |iva|2|application/xml|
|null| |3|application/xml|
|null|null|3|application/xml|
||null|3|application/xml|
|some|test|0|application/xml|
