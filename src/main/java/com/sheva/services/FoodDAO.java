package com.sheva.services;

import com.sheva.data.Food;
import com.sheva.db.Database;
import com.sheva.api.exceptions.EntityNotFoundException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.ws.rs.WebApplicationException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO class for Food entities manipulations.
 *
 * Created by Sheva on 9/29/2016.
 */
public class FoodDAO implements AbstractDAO<Food> {

    private static final Logger logger = Logger.getLogger(FoodDAO.class.getName());

    @Override
    public SessionFactory getFactory() {
        return Database.getInstance().getFactory();
    }

    @Override
    public Class getEntityClass() {
        return Food.class;
    }

    public Food updateById(final int id, final String name) throws WebApplicationException {
        return executeQuery((Session session) -> {
            Food food = (Food) session.createQuery("from Food where id=:id ").setParameter("id", id).uniqueResult();

            if (food == null) {
                logger.log(Level.WARNING, String.format("Entity %s was not found by id:%d.", Food.class, id));
                throw new EntityNotFoundException(Food.class, "id", id,
                        new IllegalArgumentException("Attempt to create delete event with null entity."));
            }

            food.setName(name);

            session.update(food);

            return food;
        });
    }

    @SuppressWarnings("unchecked")
    public List<Food> findByName(String value) throws HibernateException {
        return searchByParams(new HashMap<String, Object>() {{ put("name", value); }});
    }

    @Override
    public Food update(Food food) throws HibernateException {
        return executeQuery((Session session)-> {session.update(food); return food;});
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public Food create(Food entity) throws HibernateException {
        throw new UnsupportedOperationException("Create method is not allowed. Entity created during person persistence.");
    }

    @Override
    public void delete(Food entity) throws HibernateException {
        throw new UnsupportedOperationException("Delete method is not allowed. Entity deleted during person deletion.");
    }
}
