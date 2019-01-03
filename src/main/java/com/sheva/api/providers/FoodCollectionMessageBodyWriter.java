package com.sheva.api.providers;

import com.sheva.data.Food;

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

import static com.sheva.utils.ApplicationHelper.getTypeOfParameterByIndexForClass;

/**
 * Writer and JSON compositor for Food.class collection.
 *
 * Created by Sheva on 10/3/2016.
 */
@Provider
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class FoodCollectionMessageBodyWriter extends CollectionMessageBodyWriter<Food>
        implements MessageBodyWriter<ArrayList<Food>> {

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return List.class.isAssignableFrom(aClass) &&
                Food.class == getTypeOfParameterByIndexForClass(type, 0);
    }

    @Override
    public long getSize(ArrayList<Food> foods, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(ArrayList<Food> list, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream)
            throws WebApplicationException {
        writeTo(list, mediaType, outputStream);
    }

    @Override
    protected Class getEntityClass() {
        return Food.class;
    }
}
