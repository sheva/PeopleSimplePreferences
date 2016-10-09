package com.sheva.jbehave.stories;

import com.sheva.db.DatabaseTestHelper;
import org.jbehave.core.annotations.*;
import org.jbehave.core.steps.Steps;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import static com.sheva.jbehave.stories.AppStories.getTarget;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import static com.sheva.db.DatabaseTestHelper.findFoodById;

/**
 * 'foodlist' resource DELETE scenario.
 *
 * Created by Sheva on 10/5/2016.
 */
public class FoodDeleteScenarios extends Steps {

    private Invocation requestBuilder;

    @BeforeScenario(uponType = ScenarioType.ANY)
    public void beforeEachExampleScenario() throws Exception {
        DatabaseTestHelper.loadTestData();
    }

    @AfterScenario(uponType = ScenarioType.ANY)
    public void afterAnyScenario() throws Exception {
        DatabaseTestHelper.deleteAllData();
    }

    @Given("DELETE request send for specific food with <id>. Request media type supported <mediaType>.")
    public void givenCreateNewPersonRequestWithParams(@Named("id") String id,
                                                      @Named("mediaType") String mediaType) {
        requestBuilder = getTarget().path("foodlist/" + id).request(mediaType).buildDelete();
    }

    @When("food record with <id> exists in database.")
    public void whenPersonExists(@Named("id") int id) {
        assertNotNull(findFoodById(id));
    }

    @Then("bad request error $statusCode returned on attempt to delete food entity.")
    public void thenMethodNotSupportedForCreate(@Named("statusCode") int statusCode) throws Exception {
        Response response = requestBuilder.invoke();
        assertEquals(statusCode, response.getStatus());
    }
}
