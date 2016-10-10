package com.sheva.api.providers.json;

import com.google.gson.*;
import com.sheva.api.exceptions.InvalidRequestDataException;
import com.sheva.api.providers.xml.JaxbMarshallerProvider;
import com.sheva.db.Database;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Customized serialization processes of LocalDate.class by gson builder.
 *
 * Created by Sheva on 10/3/2016.
 */
public class LocalDateJsonAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    private static final Logger logger = Logger.getLogger(JaxbMarshallerProvider.class.getName());

    private static final String DATE_FORMAT = Database.getInstance().getDatabaseDateFormat();

    @Override
    public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!(jsonElement instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }

        String dateStr = jsonElement.getAsString();

        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT));
        } catch (DateTimeParseException e) {
            logger.log(Level.WARNING, "Invalid color property value found in request " + dateStr);
            throw new InvalidRequestDataException(null, "date", dateStr, e);
        }
    }

    @Override
    public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext jsonSerializationContext) {
        String dateFormatAsString = localDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        return new JsonPrimitive(dateFormatAsString);
    }
}
