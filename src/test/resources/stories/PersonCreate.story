Story: RESTful service should support CREATE requests for 'people' resource.

Scenario: Successful scenario for add new person.

Given create new person request with <firstName>, <lastName>, <dateOfBirth> with media type <mediaType>.
When person with <firstName>, <lastName>, <dateOfBirth> does not exists in database.
Then new person response with status code 201 returned for <firstName>, <lastName>, <dateOfBirth> with media type <mediaType>.

Examples:
|firstName|lastName|dateOfBirth|mediaType|
|test2|testLast2|1950-02-22|application/json|
|test1|testLast1|1950-01-22|application/xml|

Scenario: Check that Error 409 Conflict response returned if try to create with data with already existing person.

Given create person request with <firstName>, <lastName>, <dateOfBirth> with media type <mediaType>.
When person with <firstName>, <lastName>, <dateOfBirth> exists in database.
Then response 409 with <mediaType> returned.

Examples:
|firstName|lastName|dateOfBirth|mediaType|
|Vasya|Ivanov|1985-12-18|application/json|
|Irena|Ivanov|1985-03-23|application/xml|