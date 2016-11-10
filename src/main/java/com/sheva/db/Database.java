package com.sheva.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

/**
 * Class provides connection with database.
 *
 * Created by Sheva on 9/29/2016.
 */
public enum Database {

    INSTANCE;

    private final SessionFactory factory;

    Database() {
        Configuration configuration = new Configuration() {
            @Override
            public Configuration mergeProperties(Properties properties) {
                properties.entrySet().forEach(
                        propFromFile -> getProperties().entrySet().stream().
                                filter(property -> property.getValue()!= null && property.getValue().toString().equalsIgnoreCase("${" + propFromFile.getKey() + "}")).
                                forEach(property -> setProperty((String) property.getKey(), (String) propFromFile.getValue())));
                return this;
            }
        }.configure().mergeProperties(PropertiesFileResolver.INSTANCE.getProperties());
        factory = configuration.buildSessionFactory();
    }

    public SessionFactory getFactory() {
        return factory;
    }
}
