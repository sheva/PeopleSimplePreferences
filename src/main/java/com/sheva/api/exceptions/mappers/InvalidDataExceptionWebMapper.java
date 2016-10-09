package com.sheva.api.exceptions.mappers;

import com.sheva.api.exceptions.InvalidRequestDataException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * Created by Sheva on 10/7/2016.
 */
@Provider
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class InvalidDataExceptionWebMapper implements ExceptionMapper<InvalidRequestDataException> {

    @Override
    public Response toResponse(InvalidRequestDataException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(e.getException()).build();
    }
}