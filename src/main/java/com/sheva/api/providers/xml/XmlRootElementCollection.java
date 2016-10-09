package com.sheva.api.providers.xml;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Identifies XML container element name for collection of XML elements.
 *
 * Created by Sheva on 10/4/2016.
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface XmlRootElementCollection {

    /**
     * namespace name of the XML collection elements.
     */
    String namespace() default "";

    /**
     * local name of the XML collection elements.
     */
    String name();
}
