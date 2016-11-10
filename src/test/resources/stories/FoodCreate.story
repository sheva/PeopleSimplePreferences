Story: RESTful service should not support POST requests on 'foodlist' resource.

Scenario: Attempt to create specific food.

Given create new food request with <name> in <mediaType>.
When food with <name> does not exists in database.
Then method not allowed error 405 returned on attempt to create food entity.

Examples:
|name|mediaType|
|someTestName|application/xml|
|someTestName|application/json|
