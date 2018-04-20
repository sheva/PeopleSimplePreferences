Story: Food resource should support UPDATE operations and handle them properly.

Scenario: Food record exists. Update request performs properly.

Given update request for specific food with id=<id>. Field <field> needs to be updated with <newValue>. Request supports <mediaType> media type.
When food with id=<id> and <field>=<oldValue> pair exists in database.
Then food information updated properly with <field>=<newValue> pair. Response 200 in <mediaType>.

Examples:
|id|field|oldValue|newValue|mediaType|
|5|name|chocolate|someTest|application/json|
|7|name|some tasty|someTest|application/xml|

Scenario: Check that Error 404 response returned if try to update non-existing food record.

Given update request to specific food with id=<id>. Request supports <mediaType> media type.
When specified food with id=<id> does not exist in database.
Then error with 404 returned instead on attempt to update food. Response in <mediaType>.

Examples:
|id|mediaType|
|123|application/xml|
|234|application/json|

Scenario: Success if update food entity with duplicated data.

Given request to update food, that has id=<id> and <field>=<oldValue>, and set new value <field>=<newValue>. Request supports <mediaType> media type.
When food with id=<id> exists in the database and another food record with <field>=<oldValue> also.
Then response 200 returned with corresponding updated food with id=<id> and <field>=<newValue>. Response in <mediaType> media type.

Examples:
|id|field|oldValue|newValue|mediaType|
|7|name|some tasty|shrimp salad|application/xml|
|5|name|chocolate|fondu|application/json|