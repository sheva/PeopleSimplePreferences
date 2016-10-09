package com.sheva.jbehave.stories;

import com.sheva.api.exceptions.AlreadyExistsException;
import com.sheva.data.Color;
import com.sheva.data.Food;
import com.sheva.data.Person;
import com.sheva.db.Database;
import com.sheva.services.PersonDAO;
import org.jbehave.core.annotations.*;
import org.jbehave.core.steps.Steps;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static com.sheva.db.DatabaseTestHelper.*;
import static com.sheva.jbehave.stories.AppStories.getTarget;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class PersonCreateScenarios extends Steps {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Database.getInstance().getDatabaseDateFormat());

    private Invocation requestBuilder;

    @BeforeScenario(uponType = ScenarioType.ANY)
    public void beforeEachExampleScenario() throws Exception {
        loadTestData();
    }

    @AfterScenario(uponType = ScenarioType.ANY)
    public void afterAnyScenario() throws Exception {
        deleteAllData();
    }

    @Given("create new person request with <firstName>, <lastName>, <dateOfBirth> with media type <mediaType>.")
    public void givenCreateNewPersonRequestWithParams(@Named("firstName") String firstName,
                                                      @Named("lastName") String lastName,
                                                      @Named("dateOfBirth") String dateOfBirth,
                                                      @Named("mediaType") String mediaType) {
        Person somePerson = new PersonDAO().findById(3);
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setDateOfBirth(LocalDate.parse(dateOfBirth, dateFormatter));
        Set<Color> colors = new HashSet<Color>() {{add(Color.blue); add(Color.red);}};
        person.setColor(colors);
        Set<Food> food = new HashSet<Food>() {{add(new Food("some"));}};
        person.setFood(food);
        requestBuilder = getTarget().path("people").request().accept(mediaType).buildPost(Entity.entity(person, mediaType));
    }

    @When("person with <firstName>, <lastName>, <dateOfBirth> does not exists in database.")
    public void whenPersonExists(@Named("firstName") String firstName,
                                 @Named("lastName") String lastName,
                                 @Named("dateOfBirth") String dateOfBirth) {
        assertTrue(findPersonByParams(firstName, lastName, dateOfBirth).isEmpty());
    }

    @Then("new person response with status code $statusCode returned for <firstName>, <lastName>, <dateOfBirth> with media type <mediaType>.")
    public void thenNewPersonSuccessfullyCreated(@Named("statusCode") int statusCode,
                                                 @Named("firstName") String firstName,
                                                 @Named("lastName") String lastName,
                                                 @Named("dateOfBirth") String dateOfBirth) throws Exception {
        Response response = requestBuilder.invoke();
        assertEquals(statusCode, response.getStatus());
        assertNull(response.getMediaType());
        Person personCreated = new PersonDAO().searchByParams(firstName, lastName, LocalDate.parse(dateOfBirth, dateFormatter)).get(0);
        assertTrue(response.getLocation().getPath().contains("people/" + personCreated.getId()));
    }

    @Given("create person request with <firstName>, <lastName>, <dateOfBirth> with media type <mediaType>.")
    public void givenPersonNewRequestWithParams(@Named("firstName") String firstName,
                                                @Named("lastName") String lastName,
                                                @Named("dateOfBirth") String dateOfBirth,
                                                @Named("mediaType") String mediaType) {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setDateOfBirth(LocalDate.parse(dateOfBirth, dateFormatter));
        Set<Color> colors = new HashSet<Color>() {{add(Color.blue); add(Color.red);}};
        person.setColor(colors);
        Set<Food> food = new HashSet<Food>() {{add(new Food("some"));}};
        person.setFood(food);
        requestBuilder = getTarget().path("people").request().accept(mediaType).buildPost(Entity.entity(person, mediaType));
    }

    @When("person with <firstName>, <lastName>, <dateOfBirth> exists in database.")
    public void whenFoodRecordExistsAlreadyInDatabaseWithParams(@Named("firstName") String firstName,
                                                                @Named("lastName") String lastName,
                                                                @Named("dateOfBirth") String dateOfBirth) {
        assertEquals(1, findPersonByParams(firstName, lastName, dateOfBirth).size());
    }

    @Then("response $statusCode with <mediaType> returned.")
    public void thenErrorOnCreatePersonRequestReturned(@Named("statusCode") int statusCode,
                                                       @Named("mediaType") String mediaType) {
        Response response = requestBuilder.invoke();
        assertEquals(statusCode, response.getStatus());
        assertEquals(mediaType, response.getMediaType().toString());
        assertNotNull(response.readEntity(AlreadyExistsException.AlreadyExists.class));
    }
}
