package com.sheva.api.providers.json;

import com.google.gson.*;
import com.sheva.api.exceptions.InvalidRequestDataException;
import com.sheva.api.providers.xml.JaxbMarshallerProvider;
import com.sheva.db.PropertiesFileResolver;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;

/**
 * Customized serialization processes of LocalDate.class by gson builder.
 *
 * Created by Sheva on 10/3/2016.
 */
public class LocalDateJsonAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    private static final Logger LOGGER = Logger.getLogger(JaxbMarshallerProvider.class.getName());
    private static final String DATE_FORMAT = PropertiesFileResolver.INSTANCE.getDatabaseDateFormat();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @Override
    public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!(jsonElement instanceof JsonPrimitive)) {
            final String message = String.format("The date should be a string value: %s.", jsonElement);
            LOGGER.log(WARNING, message);
            throw new JsonParseException(message);
        }

        String dateStr = jsonElement.getAsString();
        try {
            return LocalDate.parse(dateStr, FORMATTER);
        } catch (DateTimeParseException e) {
            LOGGER.log(WARNING, String.format("Invalid date value found in request %s.", dateStr));
            throw new InvalidRequestDataException(null, "date", dateStr, e);
        }
    }

    @Override
    public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(localDate.format(FORMATTER));
    }
}
