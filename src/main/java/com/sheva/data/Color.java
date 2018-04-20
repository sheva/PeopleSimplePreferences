package com.sheva.data;

import io.swagger.annotations.ApiModel;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Color enumeration class.
 *
 * Created by Sheva on 10/1/2016.
 */
@ApiModel
public enum Color {

    RED, ORANGE, YELLOW, GREEN, BLUE, INDIGO, VIOLET, WHITE, BLACK;

    public static String printAllValues() {
        return Stream.of(Color.values()).map(Color::name).collect(Collectors.joining(", "));
    }
}
