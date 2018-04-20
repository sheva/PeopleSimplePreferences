package com.sheva.jbehave.stories;

import com.sheva.api.exceptions.EntityNotFoundException;
import com.sheva.data.Food;
import com.sheva.data.Person;
import com.sheva.services.FoodDAO;
import org.jbehave.core.annotations.*;
import org.jbehave.core.steps.Steps;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import static com.sheva.db.DatabaseTestHelper.*;
import static com.sheva.jbehave.stories.AppStories.getTarget;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Update scenarios for 'food' resource.
 *
 * Created by Sheva on 10/5/2016.
 */
public class FoodUpdateScenarios extends Steps {

    private Invocation requestBuilder;

    @BeforeScenario(uponType = ScenarioType.ANY)
    public void beforeEachExampleScenario() throws Exception {
        loadTestData();
    }

    @AfterScenario(uponType = ScenarioType.ANY)
    public void afterAnyScenario() throws Exception {
        deleteAllData();
    }

    @Given("update request for specific food with id=<id>. Field <field> needs to be updated with <newValue>. Request supports <mediaType> media type.")
    public void getUpdateRequestOnFoodResource(@Named("id") int id,
                                               @Named("field") String field,
                                               @Named("newValue") String newValue,
                                               @Named("mediaType") String mediaType) {
        Food food = new FoodDAO().findById(id);
        if ("name".equalsIgnoreCase(field.trim())) food.setName(newValue);
        requestBuilder = getTarget().path("foodlist/" + id).request().accept(mediaType).buildPut(Entity.entity(food, mediaType));
    }


    @When("food with id=<id> and <field>=<oldValue> pair exists in database.")
    public void whenTwoFoodRecordsExistsInDb(@Named("id") int id,
                                             @Named("field") String field,
                                             @Named("oldValue") String oldValue) {
        assertEquals(1, executeSqlQuery("select * from FOOD where " + field + "='" + oldValue+"' and foodId=" + id).size());
    }

    @Then("food information updated properly with <field>=<newValue> pair. Response $statusCode in <mediaType>.")
    public void thenFoodInformationUpdatedProperlyWith(@Named("field") String field,
                                                       @Named("newValue") String newValue,
                                                       @Named("statusCode") int status,
                                                       @Named("mediaType") String mediaType) {
        Response response = requestBuilder.invoke();
        assertEquals(status, response.getStatus());
        assertEquals(mediaType, response.getMediaType().toString());
        Food food = response.readEntity(Food.class);

        switch (field) {
            case "name" : assertEquals(newValue, food.getName()); break;
        }
    }

    @Given("update request to specific food with id=<id>. Request supports <mediaType> media type.")
    public void givenUpdateRequestFoodRecordWithId(@Named("id") int id, @Named("mediaType") String mediaType){
        Food food = new FoodDAO().findById(1);
        requestBuilder = getTarget().path("foodlist/" + id).request().accept(mediaType).buildPut(Entity.entity(food, mediaType));
    }

    @When("specified food with id=<id> does not exist in database.")
    public void whenSpecifiedFoodDoesNotExistsInDatabase(@Named("id") int id) {
        assertNull(findFoodById(id));
    }

    @Then("error with $statusCode returned instead on attempt to update food. Response in <mediaType>.")
    public void thenErrorStatusOnUpdateFood(@Named("statusCode") int statusCode, @Named("mediaType") String mediaType) {
        Response response = requestBuilder.invoke();
        assertEquals(statusCode, response.getStatus());
        assertEquals(mediaType, response.getMediaType().toString());
        EntityNotFoundException.EntityNotFound error = response.readEntity(EntityNotFoundException.EntityNotFound.class);
        assertTrue(error.getMessage().contains("not found"));
        assertEquals(Food.class.getSimpleName(), error.getEntityClassFailed());
    }

    @Given("request to update food, that has id=<id> and <field>=<oldValue>, and set new value <field>=<newValue>. Request supports <mediaType> media type.")
    public void givenRequestUpdateNameDuplicates(@Named("id") int id,
                                                 @Named("field") String field,
                                                 @Named("oldValue") String oldValue,
                                                 @Named("newValue") String newValue,
                                                 @Named("mediaType") String mediaType) {
        Food food = new FoodDAO().findById(id);
        switch (field) {
            case "name" : {
                assertEquals(oldValue, food.getName());
                food.setName(newValue);
            } break;
        }

        requestBuilder = getTarget().path("foodlist/" + id).request().accept(mediaType).buildPut(Entity.entity(food, mediaType));
    }

    @When("food with id=<id> exists in the database and another food record with <field>=<oldValue> also.")
    public void whenFoodAlreadyExistsWithTheSameData(@Named("id") int id,
                                                     @Named("field") String field,
                                                     @Named("oldValue") String oldValue) {
        assertEquals(1, executeSqlQuery("select * from FOOD where " + field + "='" + oldValue+"' and foodId=" + id).size());
    }

    @Then("response $statusCode returned with corresponding updated food with id=<id> and <field>=<newValue>. Response in <mediaType> media type.")
    public void thenSuccessIfDuplicateDataForFoodEntity(@Named("id") int id,
                                                        @Named("statusCode") int statusCode,
                                                        @Named("field") String field,
                                                        @Named("newValue") String newValue,
                                                        @Named("mediaType") String mediaType) {
        Response response = requestBuilder.invoke();
        assertEquals(statusCode, response.getStatus());
        assertEquals(mediaType, response.getMediaType().toString());
        Food food = response.readEntity(Food.class);
        switch (field) {
            case "name" : assertEquals(newValue, food.getName()); break;
        }
        assertEquals(id, new FoodDAO().findByName(newValue).get(0).getId());
    }
}
