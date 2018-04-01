package com.sheva.api.exceptions.mappers;

import com.sheva.api.exceptions.EntityNotFoundException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * Not Found response if entity not found exception.
 *
 * Created by Sheva on 10/3/2016.
 */
@Provider
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class EntityNotFoundExceptionWebMapper implements ExceptionMapper<EntityNotFoundException> {

    @Override
    public Response toResponse(EntityNotFoundException e) {
        return status(NOT_FOUND).entity(e.getException()).build();
    }
}
