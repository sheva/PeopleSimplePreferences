Story: People resource should support UPDATE operations and handle them properly.

Scenario: Check that property updated correctly for existing person.

Given update request for specific person with id : <id>. <field> needs update with <newValue>. Media type used <mediaType>.
When person exists with <field>:<oldValue> pair and <id> in database.
Then person information updated properly with <field>:<newValue> pair. Response in <mediaType>.

Examples:
|id|field|oldValue|newValue|mediaType|
|2|firstName|Irena|Lena|application/xml|
|1|lastName|Ivanov|Sydorov|application/xml|
|3|dateOfBirth|1957-02-12|1956-02-23|application/xml|
|2|firstName|Irena|Lena|application/json|
|1|lastName|Ivanov|Sydorov|application/json|
|3|dateOfBirth|1957-02-12|1956-02-23|application/json|

Scenario: Check that Error 404 response returned if try to update non-existing person.

Given update request to specific person with id=<id>. Request supported type <mediaType>.
When specific person does not exist with id=<id>.
Then 404 Not Found error returned instead. Response in <mediaType>.

Examples:
|id|mediaType|
|1234|application/json|
|2345|application/xml|

Scenario: Check that Error 409 Conflict response returned if try to update with data with already existing person.

Given update person with id=<id>. Fields to update firstName=<firstName> and lastName=<lastName> and dateOfBirth=<dateOfBirth>. Request supported type <mediaType>.
When specific person exists with id=<id> and person with firstName=<firstName> and lastName=<lastName> and dateOfBirth=<dateOfBirth> also exists.
Then error with 409 returned instead. Response in <mediaType>.

Examples:
|id|firstName|lastName|dateOfBirth|mediaType|
|2|Vasya|Ivanov|1985-12-18|application/xml|
|1|Irena|Ivanov|1985-03-23|application/json|
