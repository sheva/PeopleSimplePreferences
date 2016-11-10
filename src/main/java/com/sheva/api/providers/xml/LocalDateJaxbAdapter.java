package com.sheva.api.providers.xml;

import com.sheva.api.exceptions.InvalidRequestDataException;
import com.sheva.db.PropertiesFileResolver;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * XML Adapter class for String <-> LocalDate transfers.
 * Used by JAXB during marshaling/unmarshaling.
 *
 * Created by Sheva on 10/3/2016.
 */
public class LocalDateJaxbAdapter extends XmlAdapter<String, LocalDate> {

    private static final Logger logger = Logger.getLogger(LocalDateJaxbAdapter.class.getName());

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(PropertiesFileResolver.INSTANCE.getDatabaseDateFormat());

    @Override
    public LocalDate unmarshal(String localDateStr) throws Exception {
        try {
            if (StringUtils.trimToNull(localDateStr) == null) return null;
            return LocalDate.parse(localDateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            logger.log(Level.WARNING, "Invalid color property value found in request " + localDateStr);
            throw new InvalidRequestDataException(null, "date", localDateStr, e);
        }
    }

    @Override
    public String marshal(LocalDate localDate) throws Exception {
        if (localDate == null) return null;
        return localDate.format(DATE_FORMATTER);
    }
}
