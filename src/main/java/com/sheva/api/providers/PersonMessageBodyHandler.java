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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides custom writing Person beans to JSON in order to avoid recursion caused by Many-To-Many relationship.
 *
 * Created by Sheva on 10/3/2016.
 */
@Provider
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class PersonMessageBodyHandler implements MessageBodyWriter<Person>, MessageBodyReader<Person> {

    private static final Logger logger = Logger.getLogger(PersonMessageBodyHandler.class.getName());

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return type == Person.class;
    }

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return type == Person.class;
    }

    @Override
    public long getSize(Person person, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public Person readFrom(Class<Person> aClass, Type type, Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> multivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
            return readJson(inputStream);
        } else if (mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
            return readXml(inputStream);
        } else {
            logger.log(Level.WARNING, "Request with not supported media type was called: " + mediaType);
            throw new WebApplicationException("Unsupported media type " + mediaType.toString());
        }
    }

    private Person readJson(InputStream inputStream) throws IOException {
        try (InputStreamReader streamReader = new InputStreamReader(inputStream)) {
            Gson gson = new GsonBuilder().create();
            Person person = gson.fromJson(streamReader, Person.class);
            resetFoodElements(person);
            logger.log(Level.FINEST, String.format("Deserialized from JSON Person.class object <person:id=%d> : %s", person.getId(), person));
            return person;
        }
    }

    private Person readXml(InputStream inputStream) throws WebApplicationException {
        try {
            Person person = (Person) JaxbMarshallerProvider.getInstance().getUnmarshaller().unmarshal(inputStream);
            resetFoodElements(person);
            return person;
        } catch (JAXBException jaxbException) {
            Throwable cause = jaxbException.getCause();
            if (cause.getCause() instanceof InvalidRequestDataException) {
                logger.log(Level.WARNING, "Invalid request data found in request ");
                throw new InvalidRequestDataException((InvalidRequestDataException) cause.getCause());
            }

            logger.log(Level.WARNING, "Error unmarshalling object. " + jaxbException.getMessage(), jaxbException);
            throw new WebApplicationException("Deserialized from XML failed ");
        }
    }

    @Override
    public void writeTo(Person person, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
            writeJson(person, outputStream);
        } else if (mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE)) {
            writeXml(person, outputStream);
        } else {
            logger.log(Level.WARNING, "Request with not supported media type was called: " + mediaType);
            throw new WebApplicationException("Unsupported media type " + mediaType.toString());
        }
    }

    private void writeJson(Person person, OutputStream outputStream) throws IOException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(person);
        logger.log(Level.FINEST, String.format("Constructed object for <person:id=%d> : %s", person.getId(), json));
        outputStream.write(json.getBytes());
    }

    private void writeXml(Person person, OutputStream outputStream) {
        try {
            JaxbMarshallerProvider.getInstance().getMarshaller().marshal(person, outputStream);
        } catch (JAXBException jaxbException) {
            logger.log(Level.SEVERE, String.format("Error constructing object for <person:id=%d> : %s", person.getId(), jaxbException));
            throw new WebApplicationException("Error serializing a Person.class to the output stream", jaxbException);
        }
    }

    private void resetFoodElements(Person person) {
        Set<Food> newFavoriteFood = new HashSet<>();
        person.getFood().forEach((food) -> {newFavoriteFood.add(new Food(food.getName()));});
        person.setFood(newFavoriteFood);
    }
}
