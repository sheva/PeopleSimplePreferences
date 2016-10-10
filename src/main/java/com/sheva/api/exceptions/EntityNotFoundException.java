package com.sheva.api.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Exception for entity not found in database ever
 * Created by Sheva on 10/2/2016.
 */
public class EntityNotFoundException extends WebApplicationException {

    private EntityNotFound exception;

    public EntityNotFoundException(Class clazz, String key, Integer value, Throwable cause) {
        super("Entity not found.", cause);
        this.exception = new EntityNotFound(getMessage(), clazz.getSimpleName(), key, value);
    }

    public EntityNotFoundException(Class clazz, String key, Integer value) {
        this(clazz, key, value, null);
    }

    public EntityNotFound getException() {
        return exception;
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class EntityNotFound implements Serializable {

        @XmlElement private String message;
        @XmlElement private String entityClassFailed;
        @XmlElement private String key;
        @XmlElement private Integer value;

        EntityNotFound() {} // Required by JAXB.

        EntityNotFound(String message, String entityClassFailed, String key, Integer value) {
            this.message = message;
            this.entityClassFailed = entityClassFailed;
            this.key = key;
            this.value = value;
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

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "EntityNotFound{" +
                    "message='" + message + '\'' +
                    ", entityClassFailed='" + entityClassFailed + '\'' +
                    ", key='" + key + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
}
