package com.sheva.api.exceptions.mappers;

import com.sheva.api.exceptions.AlreadyExistsException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
        return Response.status(Response.Status.CONFLICT).entity(e.getException()).build();
    }
}

