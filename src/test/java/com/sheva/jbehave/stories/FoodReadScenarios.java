package com.sheva.jbehave.stories;

import com.sheva.db.DatabaseTestHelper;
import com.sheva.services.FoodDAO;

import org.jbehave.core.annotations.*;
import org.jbehave.core.steps.Steps;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;

import static com.sheva.jbehave.stories.AppStories.getTarget;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static com.sheva.db.DatabaseTestHelper.*;

/**
 * 'food' resource READ scenarios.
 *
 * Created by Sheva on 10/4/2016.
 */
public class FoodReadScenarios extends Steps {

    private Invocation requestBuilder;

    @BeforeScenario(uponType = ScenarioType.ANY)
    public void beforeEachExampleScenario() throws Exception {
        loadTestData();
    }

    @AfterScenario(uponType = ScenarioType.ANY)
    public void afterAnyScenario() throws Exception {
        deleteAllData();
    }

    @Given("GET request to 'foodlist' resource with <mediaType>.")
    public void getRequestTofoodlistResource(@Named("mediaType") String mediaType) {
        requestBuilder = getTarget().path("foodlist").request(mediaType).buildGet();
    }

    @When("there are $amount food records in the database.")
    public void whenThereIsAmountOfFood(@Named("amount") int amount) {
        List people = DatabaseTestHelper.executeSqlQuery("select * from FOOD");
        assertEquals(amount, people.size());
    }

    @Then("response contains food collection in format <mediaType> and status code $statusCode.")
    public void thenResponseContainsCollectionOfFood(@Named("mediaType") String mediaType,
                                                     @Named("statusCode") int statusCode) {
        Response response = requestBuilder.invoke();
        assertEquals(statusCode, response.getStatus());
        String responseStr = response.readEntity(String.class);
        assertFalse(responseStr.isEmpty());
        assertEquals(mediaType, response.getMediaType().toString());
    }

    @Given("request on 'foodlist' resource with query params name=<name> with <mediaType>.")
    public void givenSearchRequest(@Named("name") String name,
                                   @Named("mediaType") String mediaType) {
        WebTarget target = getTarget().path("foodlist");
        if (!name.equalsIgnoreCase("null")) target.queryParam("name", name);
        requestBuilder = target.request(mediaType).buildGet();
    }

    @When("there are <amount> food records in the database with name=<name>.")
    public void whenAmountValidRecordsInDb(@Named("name") String name,
                                           @Named("amount") int amount) {
        HashMap<String, Object> params = new HashMap<String, Object>() {{
            if (!name.equalsIgnoreCase("null")) put("name", name);
        }};
        List food = new FoodDAO().searchByParams(params);
        assertEquals(amount, food.size());
    }

    @Then("response contains food collection of elements in <mediaType> media format and status code $statusCode.")
    public void thenResponseContainsAmountElements(@Named("mediaType") String mediaType,
                                                   @Named("statusCode") int statusCode) {
        Response response = requestBuilder.invoke();
        assertEquals(statusCode, response.getStatus());
        assertEquals(mediaType, response.getMediaType().toString());
    }
}
