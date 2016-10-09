Story: RESTful service should not support DELETE requests on 'foodlist' resource.

Scenario: Attempt to delete specific food.

Given DELETE request send for specific food with <id>. Request media type supported <mediaType>.
When food record with <id> exists in database.
Then bad request error 400 returned on attempt to delete food entity.

Examples:
|id|mediaType|
|1|application/xml|
|2|application/json|