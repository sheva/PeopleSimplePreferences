package com.sheva.utils;

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

    private ApplicationHelper() {}

    public static Type getTypeOfParameterByIndexForClass(Type type, int typeIndex) {
        return type instanceof ParameterizedType ? ((ParameterizedType)type).getActualTypeArguments()[typeIndex] : null;
    }

    public static <A extends Annotation> A getAnnotation(Class clazz, Class<? extends Annotation> annotation) {
        return clazz != null && clazz.isAnnotationPresent(annotation) ? (A) clazz.getAnnotation(annotation) : null;
    }

    public static Class getClassOfParameterByIndexForClass(Type type, int typeIndex) {
       return getClassByType(getTypeOfParameterByIndexForClass(type, typeIndex));
    }

    public static Class getClassByType(Type type) {
        try {
            return Class.forName(type.getTypeName());
        } catch (ClassNotFoundException exception) {
            logger.log(Level.SEVERE, exception.getMessage(), exception);
        }
        return null;
    }
}
