package com.sheva.jbehave.stories;

import org.jbehave.core.annotations.*;
import org.jbehave.core.steps.Steps;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import static com.sheva.jbehave.stories.AppStories.getTarget;
import static com.sheva.db.DatabaseTestHelper.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * 'people' resource DELETE scenarios.
 *
 * Created by Sheva on 10/7/2016.
 */
public class PersonDeleteScenarios extends Steps {

    private Invocation requestBuilder;

    @BeforeScenario(uponType = ScenarioType.ANY)
    public void beforeEachExampleScenario() throws Exception {
        loadTestData();
    }

    @AfterScenario(uponType = ScenarioType.ANY)
    public void afterAnyScenario() throws Exception {
        deleteAllData();
    }

    @Given("DELETE request send for specific person with <id>. Request media type supported <mediaType>.")
    public void givenDeleteRequestForPersonWhenEntityExists(@Named("id") int id,
                                                            @Named("mediaType") String mediaType) {
        requestBuilder = getTarget().path("people/" + id).request(mediaType).buildDelete();
    }

    @When("person record with <id> exists in database.")
    public void whenPersonRecordExists(@Named("id") int id) throws Exception {
        assertNotNull(findPersonById(id));
    }

    @Then("person record successfully delete and response with $statusCode empty content returned.")
    public void thenPersonEntityDelete(@Named("statusCode") int statusCode) {
        Response response = requestBuilder.invoke();
        assertEquals(statusCode, response.getStatus());
        assertNull(response.getMediaType());
    }

    @Given("DELETE request send for specific <id> non-existing person. Request media type supported <mediaType>.")
    public void givenDeleteOnNonExistingResource(@Named("id") int id, @Named("mediaType") String mediaType) {
        requestBuilder = getTarget().path("people/" + id).request(mediaType).buildDelete();
    }

    @When("person record does not existing entity with id=<id> in database.")
    public void whenPersonRecordNotExists(@Named("id") int id) throws Exception {
        assertNull(findPersonById(id));
    }

    @Then("response $statusCode not found for delete person attempt returned instead with <mediaType>.")
    public void thenPersonErrorNotFoundReturned(@Named("statusCode") int statusCode, @Named("mediaType") String mediaType) {
        Response response = requestBuilder.invoke();
        assertEquals(statusCode, response.getStatus());
        assertEquals(mediaType, response.getMediaType().toString());
    }
}
