package com.sheva.api;

import com.sheva.data.Person;
import com.sheva.services.PersonDAO;
import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resource for operating with person entities.
 *
 * Created by Sheva on 9/28/2016.
 */
@Path("/people")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Api(value = "people", description = "Resource that provides operations with person entities",
        produces = "application/json | application/xml", consumes = "application/json | application/xml")
public class PersonResource {

    private static final Logger logger = Logger.getLogger(PersonResource.class.getName());

    private static final String ID_PATH_PATTERN = "/{id: [0-9]+}";

    private PersonDAO dao = new PersonDAO();

    @Context UriInfo uriInfo;

    @GET
    @ApiOperation(value = "Finds all people entities", notes = "Find all people entities", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success with list of people", response = List.class),
            @ApiResponse(code = 500, message = "Internal server error") }
    )
    public Response findAll() {
        logger.log(Level.FINE, "Find all people request received " + uriInfo.getRequestUri());
        List<Person> people = dao.findAll();
        return Response.ok().entity(new GenericEntity<List<Person>>(people){}).build();
    }

    @GET
    @Path(ID_PATH_PATTERN)
    @ApiOperation(value = "Returns person info.", notes = "Find person by specified id and return its information", response = Person.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success with retrieved information about person", response = Person.class),
            @ApiResponse(code = 404, message = "Person does not exist"),
            @ApiResponse(code = 500, message = "Internal server error") }
    )
    public Response findById(
            @ApiParam(value = "Index of food entity to be found", required = true) @PathParam("id") int id) {
        logger.log(Level.FINE, "Get request received" + uriInfo.getRequestUri());
        Person person = dao.findById(id);
        return Response.ok(person).build();
    }

    @GET
    @Path("/search")
    @ApiOperation(value = "Search for people by parameter.",
            notes = "Search for people by specified first or last name and return person information", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success with retrieved information about people. Multiple entities could be available", response = List.class),
            @ApiResponse(code = 404, message = "Person does not exist"),
            @ApiResponse(code = 500, message = "Internal server error") }
    )
    public Response searchByName(
            @ApiParam(value = "search substring to be matched with person's first name") @QueryParam("firstName") String firstName,
            @ApiParam(value = "search substring to be matched with person's last name") @QueryParam("lastName") String lastName) {
        logger.log(Level.FINE, String.format("Search request to find person by %s received", uriInfo.getRequestUri().getQuery()));
        List<Person> list = dao.searchByName(firstName, lastName);
        return Response.ok(new GenericEntity<List<Person>>(list){}).build();
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Creates person entity.", consumes = "application/json | application/xml",
            notes = "Create new person with specified information in request", response = URI.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success with creation of new person entity with attached food", response = URI.class),
            @ApiResponse(code = 400, message = "Invalid data request"),
            @ApiResponse(code = 409, message = "Food with given data already exist"),
            @ApiResponse(code = 500, message = "Internal server error") }
    )
    public Response create(
            @ApiParam(value = "Created person object", required = true) Person person) {
        logger.log(Level.FINE, "Create new person request received " + uriInfo.getRequestUri());
        Person personCreated = dao.create(person.getFirstName(), person.getLastName(), person.getDateOfBirth(), person.getColor(), person.getFood());
        URI newPersonUri = uriInfo.getBaseUriBuilder().path(Person.class).path("/" + personCreated.getId()).build();
        return Response.created(newPersonUri).build();
    }

    @PUT
    @Path(ID_PATH_PATTERN)
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Update person entity.", consumes = "application/json | application/xml",
            notes = "Update person entity by specified id with new information", response = Person.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success with update of person entity", response = Person.class),
            @ApiResponse(code = 400, message = "Invalid data request"),
            @ApiResponse(code = 404, message = "Person does not exist"),
            @ApiResponse(code = 409, message = "Person already exist"),
            @ApiResponse(code = 500, message = "Internal server error") }
    )
    public Response update(
            @ApiParam(value = "Index of food entity to be updated", required = true) @PathParam("id") int id,
            @ApiParam(value = "Updated person object", required = true) Person person) {
        logger.log(Level.FINE, "Update request received " + uriInfo.getRequestUri());
        person = dao.updateById(id, person);
        logger.log(Level.FINE, "Person updated " + person);
        return Response.ok(person).build();
    }

    @DELETE
    @Path(ID_PATH_PATTERN)
    @ApiOperation(value = "Delete person entity.", notes = "Delete person entity by specified id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success with update of person entity"),
            @ApiResponse(code = 404, message = "Person does not exist"),
            @ApiResponse(code = 500, message = "Internal server error") }
    )
    public Response deleteById(
            @ApiParam(value = "Index of food entity to be deleted", required = true) @PathParam("id") int id) {
        logger.log(Level.FINE, String.format("Delete request %s received", uriInfo.getRequestUri()));
        dao.deleteById(id);
        return Response.noContent().build();
    }
}
