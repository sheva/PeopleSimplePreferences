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

    private final JAXBContext jaxbContext;

    JaxbMarshallerProvider() {
        try {
            jaxbContext = JAXBContext.newInstance(Food.class, Person.class);
        } catch (JAXBException jaxbException) {
            Logger.getLogger(JaxbMarshallerProvider.class.getName()).log(Level.SEVERE, jaxbException.getMessage(), jaxbException);
            throw new WebApplicationException(jaxbException.getMessage(), jaxbException);
        }
    }

    public Marshaller getMarshaller() throws JAXBException {
        return jaxbContext.createMarshaller();
    }

    public Unmarshaller getUnmarshaller() throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setEventHandler((ValidationEvent event) -> false);
        return unmarshaller;
    }
}
