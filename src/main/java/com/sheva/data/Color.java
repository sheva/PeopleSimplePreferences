package com.sheva.data;

import io.swagger.annotations.ApiModel;

import java.util.Arrays;

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
        StringBuilder all = new StringBuilder();
        Arrays.asList(Color.values()).forEach((color) -> all.append(color).append(", "));
        return all.toString();
    }
}
