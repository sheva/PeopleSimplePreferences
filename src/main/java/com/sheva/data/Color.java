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

    red,
    orange,
    yellow,
    green,
    blue,
    indigo,
    violet;

    public static String printAllValues() {
        return Stream.of(Color.values()).map(Color::name).collect(Collectors.joining(", "));
    }
}
