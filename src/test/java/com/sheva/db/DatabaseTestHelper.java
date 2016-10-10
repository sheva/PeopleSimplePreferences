package com.sheva.db;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Helper class to perform test data manipulations with db.
 *
 * Created by Sheva on 10/1/2016.
 */
public class DatabaseTestHelper {

    private static final Logger logger = Logger.getLogger(DatabaseTestHelper.class.getName());

    private static void executeNativeQueriesFromFiles(Path... paths) throws Exception {
        SessionFactory factory = Database.getInstance().getFactory();

        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            for (Path path : paths) {
                try {
                    transaction = session.beginTransaction();

                    try (BufferedReader reader = Files.newBufferedReader(path)) {
                        List<String> sqlQueries = reader.lines().collect(Collectors.toList());
                        sqlQueries.forEach(sql -> {
                            logger.fine("Executing sql query: " + sql);
                            session.createNativeQuery(sql).executeUpdate();
                        });
                    }

                    transaction.commit();
                } catch (HibernateException e) {

                    if (transaction != null)
                        transaction.rollback();

                    if (e instanceof ConstraintViolationException && ((ConstraintViolationException) e).getConstraintName().contains("UNIQUE")) {
                        logger.log(Level.WARNING, "Seams like test data was already loaded");
                        break;
                    }

                    logger.log(Level.SEVERE, e.getMessage());
                    throw e;
                }

                logger.fine("Successfully executed " + path.getFileName().toString());
            }
        }
    }

    public static List executeSqlQuery(String sqlQuery) throws HibernateException {
        SessionFactory factory = Database.getInstance().getFactory();
        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            List result = session.createNativeQuery(sqlQuery).list();
            transaction.commit();
            return result;
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();

            logger.log(Level.SEVERE, e.toString(), e);
            throw e;
        }
    }

    public static void deleteAllData() throws Exception {
        executeNativeQueriesFromFiles(Paths.get("src/test/resources/db/deleteData.sql"));
    }

    public static void loadTestData() throws Exception {
        executeNativeQueriesFromFiles(Paths.get("src/test/resources/db/loadTestData.sql"));
    }

    public static Object findPersonById(int id) {
        List people = executeSqlQuery("select * from PERSON where personId=" + id);
        if (people.isEmpty()) return null;
        return people.get(0);
    }

    public static List findPersonByParams(String firstName, String lastName, String dateOfBirth) {
        return executeSqlQuery("select * from PERSON where firstName='" + firstName + "' and lastName='" + lastName + "' and dateOfBirth='" + dateOfBirth +"'");
    }

    public static List findFoodByName(String name) {
        return executeSqlQuery("select * from FOOD where name='" + name + "'");
    }

    public static Object findFoodById(int id) {
        List foodList = executeSqlQuery("select * from FOOD where foodId=" + id);
         if (foodList.isEmpty()) return null;
        return foodList.get(0);
    }
}
