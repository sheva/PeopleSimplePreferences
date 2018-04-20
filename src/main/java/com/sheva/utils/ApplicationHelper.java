package com.sheva.utils;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.ws.rs.WebApplicationException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utils class for Preferences application needs.
 *
 * Created by Sheva on 10/4/2016.
 */
public final class ApplicationHelper {

    private final static Logger logger = Logger.getLogger(ApplicationHelper.class.getName());

    public static Type getTypeOfParameterByIndexForClass(Type type, int typeIndex) {
        return type instanceof ParameterizedType ? ((ParameterizedType)type).getActualTypeArguments()[typeIndex] : null;
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A getAnnotation(Class clazz, Class<? extends Annotation> annotation) {
        return clazz != null && clazz.isAnnotationPresent(annotation) ? (A) clazz.getAnnotation(annotation) : null;
    }

    public static Class getEntityClass(Object obj) {
        try {
            return Class.forName(((ParameterizedTypeImpl)obj.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0].getTypeName());
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new WebApplicationException(e.getMessage(), e);
        }
    }
}
