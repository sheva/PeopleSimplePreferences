package com.sheva.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.logging.Logger;

/**
 * Class provides connection with database.
 *
 * Created by Sheva on 9/29/2016.
 */
public class Database {

    private static final Logger logger = Logger.getLogger(Database.class.getName());

    private static Database instance;
    private final SessionFactory factory;

    private Database() {
        factory = new Configuration().configure().buildSessionFactory();
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public SessionFactory getFactory() {
        return factory;
    }

    public String getDatabaseDateFormat() {
        return factory.getProperties().get("hibernate.connection.date_string_format").toString(); // TODO: set this property on application level. and add placeholder to hibernate.cfg.xml
    }
}
