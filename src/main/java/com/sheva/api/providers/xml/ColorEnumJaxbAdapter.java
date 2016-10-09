package com.sheva.api.providers.xml;

import com.sheva.api.exceptions.InvalidRequestDataException;
import com.sheva.data.Color;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JAXB Adapter for {@link com.sheva.data.Color} type.
 *
 * Created by Sheva on 10/7/2016.
 */
public class ColorEnumJaxbAdapter extends XmlAdapter<String, Color> {

    private static final Logger logger = Logger.getLogger(ColorEnumJaxbAdapter.class.getName());

    public Color unmarshal(String value) throws InvalidRequestDataException {
        try {
            return Color.valueOf(value);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid color property value found in request " + value);
            throw new InvalidRequestDataException(null, "color", value, e, "available values: " + Color.printAllValues());
        }
    }

    public String marshal(Color value) {
        return value.name();
    }
}
