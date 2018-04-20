package com.sheva.db;

import javax.ws.rs.WebApplicationException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Created by Sheva on 10/21/2016.
 */
public enum PropertiesFileResolver {

    INSTANCE;

    private final Properties properties;

    PropertiesFileResolver() {
        try {
            properties = new Properties();
            properties.load(new FileReader(Paths.get("src/main/resources/preferences.properties").toFile()));
        } catch (IOException e) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Unable to load properties for database configuration");
            throw new WebApplicationException("Unable to load properties for database configuration");
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public String getDatabaseDateFormat() {
        return properties.getProperty("hibernate.connection.date_string_format");
    }

    public String getApplicationURL() {
        return "http://" + properties.getProperty("application.host") + ":" + properties.getProperty("application.post");
    }

    public String getContextPath() {
        return properties.getProperty("application.context.path");
    }
}
