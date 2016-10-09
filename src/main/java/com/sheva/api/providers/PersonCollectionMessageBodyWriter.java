package com.sheva.api.providers;

import com.sheva.data.Person;
import com.sheva.utils.ApplicationHelper;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Constructs response's output for Food.class collections.
 *
 * Created by Sheva on 10/3/2016.
 */
@Provider
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class PersonCollectionMessageBodyWriter extends CollectionMessageBodyWriter<Person> implements MessageBodyWriter<ArrayList<Person>> {

    private static final Logger logger = Logger.getLogger(PersonCollectionMessageBodyWriter.class.getName());

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return List.class.isAssignableFrom(aClass) && Person.class == new ApplicationHelper().getTypeOfParameterByIndexForClass(type, 0);
    }

    @Override
    public long getSize(ArrayList<Person> es, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(ArrayList<Person> list, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws WebApplicationException {
        writeTo(list, mediaType, outputStream);
    }

    @Override
    protected Class getEntityClass() {
        return Person.class;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
