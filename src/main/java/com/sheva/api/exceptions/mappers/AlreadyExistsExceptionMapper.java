package com.sheva.api.exceptions.mappers;

import com.sheva.api.exceptions.AlreadyExistsException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.CONFLICT;

/**
 * Entity already exists exception response mapper.
 *
 * Created by Sheva on 10/4/2016.
 */
@Provider
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class AlreadyExistsExceptionMapper implements ExceptionMapper<AlreadyExistsException> {

    @Override
    public Response toResponse(AlreadyExistsException e) {
        return status(CONFLICT).entity(e.getException()).build();
    }
}

