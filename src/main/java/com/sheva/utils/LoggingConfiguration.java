package com.sheva.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *  Configure java util logging.
 *
 * Created by Sheva on 10/21/2016.
 */
public class LoggingConfiguration  {

    private final static Logger logger = Logger.getLogger(LoggingConfiguration.class.getName());

    public static void configure() {
        Logger log = Logger.getLogger("com.sheva");
        log.setLevel(Level.ALL);
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream(Paths.get("src/main/resources/log.properties").toFile()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during configuring logging service. " + e.getMessage(), e);
        }
    }
}
