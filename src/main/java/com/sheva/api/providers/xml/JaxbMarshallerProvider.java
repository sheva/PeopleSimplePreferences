package com.sheva.api.providers.xml;

import com.sheva.data.Food;
import com.sheva.data.Person;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates marshaller instances with defined context.
 *
 * Created by Sheva on 10/4/2016.
 */
public enum JaxbMarshallerProvider {

    INSTANCE;

    private final Logger logger = Logger.getLogger(JaxbMarshallerProvider.class.getName());
    private final JAXBContext jaxbContext;

    JaxbMarshallerProvider() {
        try {
            jaxbContext = JAXBContext.newInstance(Food.class, Person.class);
        } catch (JAXBException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new WebApplicationException(e.getMessage(), e);
        }
    }

    public Marshaller createMarshaller() throws JAXBException {
        return jaxbContext.createMarshaller();
    }

    public Unmarshaller createUnmarshaller() throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setEventHandler(e -> false);
        return unmarshaller;
    }
}
