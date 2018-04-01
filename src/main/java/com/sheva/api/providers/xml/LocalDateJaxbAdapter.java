package com.sheva.api.providers.xml;

import com.sheva.api.exceptions.InvalidRequestDataException;
import com.sheva.db.PropertiesFileResolver;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * XML Adapter class for String <-> LocalDate transfers.
 * Used by JAXB during marshaling/unmarshaling.
 *
 * Created by Sheva on 10/3/2016.
 */
public class LocalDateJaxbAdapter extends XmlAdapter<String, LocalDate> {

    private static final Logger LOGGER = Logger.getLogger(LocalDateJaxbAdapter.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(PropertiesFileResolver.INSTANCE.getDatabaseDateFormat());

    @Override
    public LocalDate unmarshal(String localDateStr) {
        try {
            return (trimToNull(localDateStr) != null) ? LocalDate.parse(localDateStr, DATE_FORMATTER) : null;
        } catch (DateTimeParseException e) {
            LOGGER.log(Level.WARNING, "Invalid color property value found in request " + localDateStr);
            throw new InvalidRequestDataException(null, "date", localDateStr, e);
        }
    }

    @Override
    public String marshal(LocalDate localDate) {
        return localDate != null ? localDate.format(DATE_FORMATTER) : null;
    }
}
