package com.sheva.services;

import com.sheva.api.exceptions.EntityNotFoundException;
import com.sheva.data.Color;
import com.sheva.data.Food;
import com.sheva.data.Person;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.ws.rs.WebApplicationException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO class for Person entities manipulations.
 *
 * Created by Sheva on 9/29/2016.
 */
public class PersonDAO extends AbstractDAO<Person> {

    private static final Logger logger = Logger.getLogger(PersonDAO.class.getName());

    @Override
    public Person create(Person person) throws HibernateException {
        return save(null, person);
    }

    @Override
    public Person update(Person person) throws HibernateException {
        return save(null, person);
    }

    @Override
    public void delete(Person person) throws HibernateException {
        delete(null, person);
    }

    @SuppressWarnings("unchecked")
    public List<Person> searchByName(String firstName, String lastName) throws HibernateException {
        return searchByParams(firstName, lastName, null);
    }

    @SuppressWarnings("unchecked")
    public List<Person> searchByParams(String firstName, String lastName, LocalDate dateOfBirth) throws HibernateException {
        return searchByParams(new HashMap<String, Object>() {{
            put("firstName", firstName);
            put("lastName", lastName);
            put("dateOfBirth", dateOfBirth);
        }});
    }

    public Person updateById(final int id, Person personFromUpdate) throws WebApplicationException {
        return executeQuery((Session session) -> {
            Person person = (Person) session.createQuery("from Person where id=:id ").setParameter("id", id).uniqueResult();

            if (person == null) {
                logger.log(Level.WARNING, String.format("Entity %s was not found by id:%d.", Person.class, id));
                throw new EntityNotFoundException(Person.class, "id", id,
                        new IllegalArgumentException("Attempt to create update event with null entity."));
            }

            person.setFirstName(personFromUpdate.getFirstName());
            person.setLastName(personFromUpdate.getLastName());
            person.setDateOfBirth(personFromUpdate.getDateOfBirth());
            person.setColor(personFromUpdate.getColor());

            Set<Food> oldFood = person.getFood();
            Set<Food> newFood = new HashSet<>(personFromUpdate.getFood());
            oldFood.addAll(newFood);
            oldFood.retainAll(newFood);

            update(session, person);

            return person;
        });
    }

    public void deleteById(final int id) throws WebApplicationException {
        executeQuery((Session session) -> {
            Person person = (Person) session.createQuery("from Person where id=:id ").setParameter("id", id).uniqueResult();

            if (person == null) {
                logger.log(Level.WARNING, String.format("Entity %s was not found by id:%d", Person.class, id));
                throw new EntityNotFoundException(Person.class, "id", id,
                        new IllegalArgumentException("Attempt to create delete event with null entity"));
            }

            delete(session, person);

            return person;
        });
    }

    public Person create(final String firstName, final String lastName, final LocalDate dateOfBirth,
                         final Set<Color> favoriteColor, final Set<Food> favoriteFood) {
        return executeQuery((Session session) -> {
            Person person = new Person();

            person.setFirstName(firstName);
            person.setLastName(lastName);
            person.setDateOfBirth(dateOfBirth);
            person.setColor(favoriteColor);
            person.setFood(favoriteFood);

            save(session, person);

            return person;
        });
    }

    private void delete(final Session predefSession, Person person) throws HibernateException {
        executeQuery((Session session) -> {
            if (predefSession != null) session = predefSession;
            session.delete(person);
            return person;
        });
    }

    private void update(final Session predefSession, Person person) throws HibernateException {
        executeQuery((Session session) -> {
            if (predefSession != null) session = predefSession;
            session.update(person);
            return person;
        });
    }

    private Person save(final Session predefSession, Person person) throws HibernateException {
        return executeQuery((Session session) -> {
            if (predefSession != null) session = predefSession;
            session.save(person);
            return person;
        });
    }
}
