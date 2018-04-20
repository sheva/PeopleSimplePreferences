package com.sheva.jbehave.stories;

import com.sheva.data.Person;
import com.sheva.db.DatabaseTestHelper;
import com.sheva.services.PersonDAO;
import org.jbehave.core.annotations.*;
import org.jbehave.core.steps.Steps;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sheva.jbehave.stories.AppStories.getTarget;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Person find stories.
 *
 * Created by Sheva on 9/27/2016.
 */
public class PersonReadScenarios extends Steps {

    private Invocation requestBuilder;

    @BeforeScenario(uponType = ScenarioType.ANY)
    public void beforeEachExampleScenario() throws Exception {
        DatabaseTestHelper.loadTestData();
    }

    @AfterScenario(uponType = ScenarioType.ANY)
    public void afterAnyScenario() throws Exception {
        DatabaseTestHelper.deleteAllData();
    }

    @Given("GET request to resource 'people'. Request supported <mediaType>.")
    public void getRequestToResourcePeople(@Named("mediaType") String mediaType) {
        requestBuilder = getTarget().path("people").request(mediaType).buildGet();
    }

    @When("there is $amount people in the database.")
    public void whenThereIsAmountPeople(@Named("amount") int amount) {
        List people = DatabaseTestHelper.executeSqlQuery("select * from PERSON");
        assertEquals(amount, people.size());
    }

    @Then("response contains people collection with $statusCode and <mediaType>.")
    public void thenResponseContainsAmountPeopleInTheList(@Named("statusCode") int statusCode,
                                                          @Named("mediaType") String mediaType) {
        Response response = requestBuilder.invoke();
        assertEquals(statusCode, response.getStatus());
        String responseStr = response.readEntity(String.class);
        assertFalse(responseStr.isEmpty());
        assertEquals(mediaType, response.getMediaType().toString());
    }

    @Given("search?firstName=<firstName>&lastName=<lastName> request on 'people' resource with <mediaType>.")
    public void givenSearchRequestToPeople(@Named("firstName") String first,
                                           @Named("lastName") String last,
                                           @Named("mediaType") String mediaType) {
        WebTarget target = getTarget().path("people/search");
        if (!first.equalsIgnoreCase("null")) target = target.queryParam("firstName", first);
        if (!last.equalsIgnoreCase("null")) target = target.queryParam("lastName", last);
        requestBuilder = target.request(mediaType).buildGet();
    }

    @When("there are <amount> person records in the database with firstName=<firstName> and lastName=<lastName> pairs.")
    public void whenAmountValidPeopleRecordsInDb(@Named("firstName") String first,
                                                 @Named("lastName") String last,
                                                 @Named("amount") int amount) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            if (!first.equalsIgnoreCase("null")) put("firstName", first);
            if (!last.equalsIgnoreCase("null")) put("lastName", last);
        }};
        List people = new PersonDAO().searchByParams(params);
        assertEquals(amount, people.size());
    }


    @Then("response contains people collection with <amount> of elements with firstName=<firstName> and lastName=<lastName> " +
            "in <mediaType> media format and status code $statusCode.")
    public void thenResponseContainsPersonAmountElements(@Named("amount") int amount,
                                                         @Named("firstName") String first,
                                                         @Named("lastName") String last,
                                                         @Named("mediaType") String mediaType,
                                                         @Named("statusCode") int statusCode) {
        Response response = requestBuilder.invoke();
        assertEquals(statusCode, response.getStatus());
        assertEquals(mediaType, response.getMediaType().toString());
        List<Person> people =  response.readEntity(new GenericType<List<Person>>(){});
        assertEquals(amount, people.size());
        List<Person> peopleFromDb = getPersonsByParams(first, last);
        assertTrue(peopleFromDb.containsAll(people));
    }

    private List<Person> getPersonsByParams(@Named("firstName") final String first, @Named("lastName") final String last) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            if (!first.equalsIgnoreCase("null")) put("firstName", first);
            if (!last.equalsIgnoreCase("null")) put("lastName", last);
        }};
        return new PersonDAO().searchByParams(params);
    }
}
