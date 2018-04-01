package com.sheva.api.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Exception thrown in case of corrupted request body.
 *
 * Created by Sheva on 10/7/2016.
 */
public class InvalidRequestDataException extends WebApplicationException {

    private InvalidRequestDataException.InvalidRequestData exception;

    public InvalidRequestDataException(String entityClass, String key, Object value, Throwable cause) {
        this(entityClass, key, value, cause, null);
    }

    public InvalidRequestDataException(String entityClass, String key, Object value, Throwable cause, String description) {
        super("Invalid request data", cause);
        this.exception = new InvalidRequestDataException.InvalidRequestData(cause.getMessage(), entityClass, key, value, description);
    }

    public InvalidRequestDataException(InvalidRequestDataException e) {
        super("Invalid request data");
        this.exception = e.getException();
    }

    public InvalidRequestDataException.InvalidRequestData getException(){
        return exception;
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class InvalidRequestData implements Serializable {

        @XmlElement private String message;
        @XmlElement private String entityClassFailed;
        @XmlElement private String key;
        @XmlElement private Object value;
        @XmlElement private String description;

        InvalidRequestData() {} // Required by JAXB.

        InvalidRequestData(String message, String entityClassFailed, String key, Object value, String description) {
            this.message = message;
            this.entityClassFailed = entityClassFailed;
            this.key = key;
            this.value = value;
            this.description = description;
        }

        @Override
        public String toString() {
            return "InvalidRequestData{" +
                    "message='" + message + '\'' +
                    ", entityClassFailed='" + entityClassFailed + '\'' +
                    ", key='" + key + '\'' +
                    ", value=" + value +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
}
