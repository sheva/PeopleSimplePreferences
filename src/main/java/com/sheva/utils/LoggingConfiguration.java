package com.sheva.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.LogManager;

/**
 *  Configure java util logging.
 *
 * Created by Sheva on 10/21/2016.
 */
public class LoggingConfiguration  {

    public static void configure() {
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream(Paths.get("src/main/resources/log.properties").toFile()));
        } catch (IOException e) {
            System.out.println("Error during configuring logging service. " + e.getCause());
        }
    }
}
