package com.sheva.api.providers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sheva.api.exceptions.InvalidRequestDataException;
import com.sheva.api.providers.xml.JaxbMarshallerProvider;
import com.sheva.data.Food;
import com.sheva.data.Person;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static java.util.logging.Level.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;

/**
 * Provides custom writing Person beans to JSON in order to avoid recursion caused by Many-To-Many relationship.
 *
 * Created by Sheva on 10/3/2016.
 */
@Provider
@Produces({APPLICATION_JSON, APPLICATION_XML})
@Consumes({APPLICATION_JSON, APPLICATION_XML})
public class PersonMessageBodyHandler implements MessageBodyWriter<Person>, MessageBodyReader<Person> {

    private static final Logger logger = Logger.getLogger(PersonMessageBodyHandler.class.getName());
    private static final Type clazz = Person.class;

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return type == clazz;
    }

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return type == clazz;
    }

    @Override
    public long getSize(Person person, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public Person readFrom(Class<Person> aClass, Type type, Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> multivaluedMap, InputStream input)
            throws IOException, WebApplicationException {
        if (mediaType.isCompatible(APPLICATION_JSON_TYPE)) {
            return readJson(input);
        } else if (mediaType.isCompatible(APPLICATION_XML_TYPE)) {
            return readXml(input);
        } else {
            logger.log(WARNING, String.format("Media type '%s' is not supported.", mediaType));
            throw new WebApplicationException(String.format("Unsupported media type '%s'.", mediaType));
        }
    }

    private Person readJson(InputStream input) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(input)) {
            final Gson gson = new GsonBuilder().create();
            final Person person = gson.fromJson(reader, clazz);
            resetFoodElements(person);
            logger.log(FINEST, String.format("Deserialization from JSON object <person:id=%d>: %s failed.", person.getId(), person));
            return person;
        }
    }

    private Person readXml(InputStream stream) throws WebApplicationException {
        try {
            final Person person = (Person) JaxbMarshallerProvider.INSTANCE.createUnmarshaller().unmarshal(stream);
            resetFoodElements(person);
            logger.log(FINEST, String.format("Deserialization from XML object <person:id=%d>: %s failed.", person.getId(), person));
            return person;
        } catch (JAXBException e) {
            Throwable cause = e.getCause();
            if (cause.getCause() instanceof InvalidRequestDataException) {
                logger.log(WARNING, "Invalid request data found in request.");
                throw new InvalidRequestDataException((InvalidRequestDataException) cause.getCause());
            }
            logger.log(WARNING, String.format("Error unmarshalling object. Reason: '%s'.", e.getMessage()), e);
            throw new WebApplicationException("Deserialization from XML failed.");
        }
    }

    @Override
    public void writeTo(Person person, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> multivaluedMap, OutputStream output)
            throws IOException, WebApplicationException {
        if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
            writeJson(person, output);
        } else if (mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
            writeXml(person, output);
        } else {
            logger.log(WARNING, String.format("Media type '%s' is not supported.", mediaType));
            throw new WebApplicationException(String.format("Unsupported media type '%s'.", mediaType));
        }
    }

    private void writeJson(Person person, OutputStream outputStream) throws IOException {
        final Gson gson = new GsonBuilder().create();
        final String personInJson = gson.toJson(person);
        outputStream.write(personInJson.getBytes());
        logger.log(FINEST, String.format("Serialized object for <person:id=%d> : %s.", person.getId(), personInJson));
    }

    private void writeXml(Person person, OutputStream outputStream) {
        try {
            JaxbMarshallerProvider.INSTANCE.createMarshaller().marshal(person, outputStream);
            logger.log(FINEST, String.format("Serialized object for <person:id=%d> : %s.", person.getId(), person));
        } catch (JAXBException jaxbException) {
            logger.log(SEVERE, String.format("Error constructing object for <person:id=%d>: %s.", person.getId(), jaxbException));
            throw new WebApplicationException("Error serializing to the output stream.", jaxbException);
        }
    }

    private void resetFoodElements(Person person) {
        final Set<Food> newFavoriteFood = new HashSet<>();
        person.getFood().forEach(f -> newFavoriteFood.add(new Food(f.getName())));
        person.setFood(newFavoriteFood);
    }
}
