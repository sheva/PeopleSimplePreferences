package com.sheva.api.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Unique constraint violation exception.
 *
 * Created by Sheva on 10/4/2016.
 */
public class AlreadyExistsException extends WebApplicationException {

    private AlreadyExistsException.AlreadyExists exception;

    public AlreadyExistsException(Class clazz, Throwable cause) {
        super("Entity already exists.", cause);
        this.exception = new AlreadyExistsException.AlreadyExists(getMessage(), clazz.getSimpleName());
    }

    public AlreadyExistsException(Class clazz) {
        this(clazz, null);
    }

    public AlreadyExistsException.AlreadyExists getException(){
        return exception;
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AlreadyExists implements Serializable {

        @XmlElement private String message;
        @XmlElement private String entityClassFailed;

        AlreadyExists() {} // Required by JAXB.

        AlreadyExists(String message, String entityClassFailed) {
            this.message = message;
            this.entityClassFailed = entityClassFailed;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getEntityClassFailed() {
            return entityClassFailed;
        }

        public void setEntityClassFailed(String entityClassFailed) {
            this.entityClassFailed = entityClassFailed;
        }

        @Override
        public String toString() {
            return "AlreadyExists{" +
                    "message='" + message + '\'' +
                    ", entityClassFailed='" + entityClassFailed + '\'' +
                    '}';
        }
    }
}
