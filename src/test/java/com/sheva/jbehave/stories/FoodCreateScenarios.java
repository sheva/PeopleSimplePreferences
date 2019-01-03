package com.sheva.jbehave.stories;

import com.sheva.data.Food;
import org.jbehave.core.annotations.*;
import org.jbehave.core.steps.Steps;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import static com.sheva.db.DatabaseTestHelper.*;
import static com.sheva.jbehave.stories.AppStories.getTarget;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test scenario for CREATE requests on 'foodlist' resource.
 *
 * Created by Sheva on 10/5/2016.
 */
public class FoodCreateScenarios extends Steps {

    private Invocation requestBuilder;

    @BeforeScenario(uponType = ScenarioType.ANY)
    public void beforeEachExampleScenario() throws Exception {
        loadTestData();
    }

    @AfterScenario(uponType = ScenarioType.ANY)
    public void afterAnyScenario() throws Exception {
        deleteAllData();
    }

    @Given("create new food request with <name> in <mediaType>.")
    public void givenCreateNewPersonRequestWithParams(@Named("name") String name,
                                                      @Named("mediaType") String mediaType) {
        requestBuilder = getTarget().path("foodlist").request().accept(mediaType).
                buildPost(Entity.entity(new Food(name), mediaType));
    }

    @When("food with <name> does not exists in database.")
    public void whenPersonExists(@Named("name") String name)  {
        assertTrue(findFoodByName(name).isEmpty());
    }

    @Then("method not allowed error $statusCode returned on attempt to create food entity.")
    public void thenMethodNotSupportedForCreate(@Named("statusCode") int statusCode) throws Exception {
        Response response = requestBuilder.invoke();
        assertEquals(statusCode, response.getStatus());
    }
}
