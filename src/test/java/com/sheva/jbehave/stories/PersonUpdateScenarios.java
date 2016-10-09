package com.sheva.jbehave.stories;

import com.sheva.api.exceptions.AlreadyExistsException;
import com.sheva.api.exceptions.EntityNotFoundException;
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
import java.util.List;

import static com.sheva.db.DatabaseTestHelper.*;
import static com.sheva.jbehave.stories.AppStories.getTarget;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


/**
 * Update person stories mapping class.
 *
 * Created by Sheva on 9/27/2016.
 */
public class PersonUpdateScenarios extends Steps {

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

    @Given("update request for specific person with id : <id>. <field> needs update with <newValue>. Media type used <mediaType>.")
    public void updateSpecificPersonByFieldValue(@Named("id") int id,
                                                 @Named("field") String field,
                                                 @Named("newValue") String value,
                                                 @Named("mediaType") String mediaType) throws Exception {
        Person person = new PersonDAO().findById(id);
        switch (field) {
            case "firstName" : person.setFirstName(value); break;
            case "lastName" : person.setLastName(value); break;
            case "datOfBirth" : person.setDateOfBirth(LocalDate.parse(value, dateFormatter)); break;
        }
        requestBuilder = getTarget().path("people/" + id).request().accept(mediaType).buildPut(Entity.entity(person, mediaType));
    }

    @When("person exists with <field>:<oldValue> pair and <id> in database.")
    public void personExistsWithPairInDatabase(@Named("id") int id,
                                               @Named("field") String field,
                                               @Named("oldValue") String oldValue) throws Exception {
        List people = executeSqlQuery("select * from PERSON where " + field + "='" + oldValue+"' and personId=" + id);
        assertEquals(1, people.size());
    }

    @Then("person information updated properly with <field>:<newValue> pair. Response in <mediaType>.")
    public void personInfoUpdatedOk(@Named("field") String field,
                                    @Named("newValue") String newValue,
                                    @Named("mediaType") String mediaType) {
        Response response = requestBuilder.invoke();
        assertEquals(200, response.getStatus());
        assertEquals(mediaType, response.getMediaType().toString());
        Person person = response.readEntity(Person.class);

        switch (field) {
            case "firstName" : assertEquals(newValue, person.getFirstName()); break;
            case "lastName" : assertEquals(newValue, person.getLastName()); break;
            case "datOfBirth" : {
                LocalDate dateOfBirth = person.getDateOfBirth();
                assertEquals(newValue, dateOfBirth.format(dateFormatter));
                assertNotNull(person.getAge());
            } break;
        }

        assertNotNull(person.getColor());
        assertNotNull(person.getFood());
    }

    @Given("update request to specific person with id=<id>. Request supported type <mediaType>.")
    public void givenRequestToUpdateForNotExistingPerson(@Named("id") int id,
                                                         @Named("mediaType") String mediaType) throws Exception {
        Person person = new PersonDAO().findById(1); // Just to create object
        requestBuilder = getTarget().path("people/" + id).
                request().accept(mediaType).buildPut(Entity.entity(person, mediaType));
    }

    @When("specific person does not exist with id=<id>.")
    public void whenSpecificPersonNotExistsInDb(@Named("id") int id) throws Exception {
        assertNull(findPersonById(id));
    }

    @Then("$statusCode Not Found error returned instead. Response in <mediaType>.")
    public void errorNotFoundResponse(@Named("statusCode") int statusCode,
                                      @Named("mediaType") String mediaType) {
        Response response = requestBuilder.invoke();
        assertEquals(statusCode, response.getStatus());
        assertEquals(mediaType, response.getMediaType().toString());
        EntityNotFoundException.EntityNotFound error = response.readEntity(EntityNotFoundException.EntityNotFound.class);
        assertTrue(error.getMessage().contains("not found"));
        assertEquals(Person.class.getSimpleName(), error.getEntityClassFailed());
    }

    @Given("update person with id=<id>. Fields to update firstName=<firstName> and lastName=<lastName> and dateOfBirth=<dateOfBirth>. Request supported type <mediaType>.")
    public void givenRequestTwoPeopleToUpdateAndUpdateFrom(@Named("id") int id,
                                                            @Named("firstName") String firstName,
                                                            @Named("lastName") String lastName,
                                                            @Named("dateOfBirth") String dateOfBirth,
                                                            @Named("mediaType") String mediaType) {
        Person person = new PersonDAO().findById(id);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setDateOfBirth(LocalDate.parse(dateOfBirth, dateFormatter));
        requestBuilder = getTarget().path("people/" + id).
                request().accept(mediaType).buildPut(Entity.entity(person, mediaType));
    }

    @When("specific person exists with id=<id> and person with firstName=<firstName> and lastName=<lastName> and dateOfBirth=<dateOfBirth> also exists.")
    public void whenTwoPersonsExist(@Named("id") int id,
                                    @Named("firstName") String firstName,
                                    @Named("lastName") String lastName,
                                    @Named("dateOfBirth") String dateOfBirth) {
        assertNotNull(findPersonById(id));
        assertEquals(1, findPersonByParams(firstName, lastName, dateOfBirth).size());
    }

    @Then("error with $statusCode returned instead. Response in <mediaType>.")
    public void thenErrorAlreadyExist(@Named("$statusCode") int statusCode,
                                      @Named("mediaType") String mediaType) {
        Response response = requestBuilder.invoke();
        assertEquals(statusCode, response.getStatus());
        assertEquals(mediaType, response.getMediaType().toString());
        AlreadyExistsException.AlreadyExists error = response.readEntity(AlreadyExistsException.AlreadyExists.class);
        assertEquals(Person.class.getSimpleName(), error.getEntityClassFailed());
    }
}
