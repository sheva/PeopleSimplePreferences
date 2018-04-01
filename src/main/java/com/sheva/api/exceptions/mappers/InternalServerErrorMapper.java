package com.sheva.api.exceptions.mappers;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

/**
 * Base wrapper class for Internal Server Errors.
 *
 * Created by Sheva on 10/3/2016.
 */
@Provider
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class InternalServerErrorMapper implements ExceptionMapper<Exception> {

    public Response toResponse(Exception ex) {
         return status(INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
    }
}