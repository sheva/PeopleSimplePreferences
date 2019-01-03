package com.sheva.api.providers.json;

import com.google.gson.*;
import com.sheva.api.exceptions.InvalidRequestDataException;
import com.sheva.data.Color;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;

public class ColorJsonAdapter implements JsonSerializer<Set<Color>>, JsonDeserializer<Set<Color>> {

    private static final Logger LOGGER = Logger.getLogger(ColorJsonAdapter.class.getName());

    @Override
    public JsonElement serialize(Set<Color> colors, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray colorsArr = new JsonArray();
        colors.forEach(c -> colorsArr.add(new JsonPrimitive(c.name())));
        return colorsArr;
    }

    @Override
    public Set<Color> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        if (!(jsonElement instanceof JsonArray)) {
            LOGGER.log(WARNING, "Invalid format of color element.");
            throw new JsonParseException("Invalid format of color element. Should be an array.");
        }

        Set<Color> colors = new HashSet<>();

        ((JsonArray) jsonElement).forEach((element) -> {
            String color = element.getAsString();
            try {
                colors.add(Color.valueOf(color.toUpperCase()));
            } catch (IllegalArgumentException e) {
                LOGGER.log(WARNING, String.format("Invalid color value found in request %s.", color));
                throw new InvalidRequestDataException(null, "color", color, e, "available values: " + Color.printAllValues());
            }
        });

        return colors;
    }
}
