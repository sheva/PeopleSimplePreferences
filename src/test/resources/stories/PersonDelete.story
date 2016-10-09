Story: RESTful service should support DELETE requests for 'people' resource.

Scenario: Delete person record in database. Supported media types XML|JSON. Response status - 204 Empty Content.

Given DELETE request send for specific person with <id>. Request media type supported <mediaType>.
When person record with <id> exists in database.
Then person record successfully delete and response with 204 empty content returned.

Examples:
|id|mediaType|
|2|application/xml|
|3|application/json|

Scenario: Delete person record in database when not exists. Supported media types XML|JSON.

Given DELETE request send for specific <id> non-existing person. Request media type supported <mediaType>.
When person record does not existing entity with id=<id> in database.
Then response 404 not found for delete person attempt returned instead with <mediaType>.

Examples:
|id|mediaType|
|566|application/xml|
|788|application/json|
