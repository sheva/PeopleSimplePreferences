package com.sheva.services;

import com.sheva.api.exceptions.InvalidRequestDataException;
import com.sheva.data.Person;
import com.sheva.api.exceptions.AlreadyExistsException;
import com.sheva.api.exceptions.EntityNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import javax.persistence.PersistenceException;
import javax.ws.rs.WebApplicationException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base DAO class for manipulating with persistent entities.
 *
 * Created by Sheva on 10/2/2016.
 */
interface AbstractDAO<E> {

    SessionFactory getFactory();

    E create(E entity) throws HibernateException;
    E update(E entity) throws HibernateException;
    void delete(E entity) throws HibernateException;
    Class getEntityClass();
    Logger getLogger();

    interface ExecutableQuery<E> {
        E execute(Session session) throws HibernateException;
    }

    default <T> T executeQuery(ExecutableQuery<T> query) throws HibernateException {

        Transaction transaction = null;

        try (Session session = getFactory().openSession()) {
            transaction = session.beginTransaction();

            T result = query.execute(session);

            transaction.commit();
            return result;

        } catch (PersistenceException e) {
            if (transaction != null)
                transaction.rollback();

            if (e.getCause() instanceof ConstraintViolationException) {

                ConstraintViolationException constraint = (ConstraintViolationException) e.getCause();
                getLogger().log(Level.WARNING, e.toString(), e);

                if  (constraint.getConstraintName().contains("UNIQUE")) {
                    throw new AlreadyExistsException(getEntityClass(), e.getCause());
                } else if (constraint.getConstraintName().contains("NOT NULL")) {
                    SQLException sqlException = ((ConstraintViolationException) e.getCause()).getSQLException();
                    throw new InvalidRequestDataException(getEntityClass().getSimpleName(), null, null, sqlException);
                }
                throw e;
            }

            getLogger().log(Level.SEVERE, e.toString(), e);
            throw e;
        }
    }

    @SuppressWarnings(value = "unchecked")
    default List<E> findAll() throws WebApplicationException {
        return executeQuery((Session session) -> session.createQuery("from " + getEntityClass().getSimpleName()).list());
    }

    @SuppressWarnings(value = "unchecked")
    default E findById(final int id) throws WebApplicationException {
        String query = String.format("from %s e where e.id=:id", getEntityClass().getSimpleName());
        E entity = executeQuery((Session session) -> (E) session.createQuery(query).setParameter("id", id).uniqueResult());

        if (entity == null) {
            getLogger().log(Level.WARNING, String.format("Entity %s was not found by id:%d.", getEntityClass(), id));
            throw new EntityNotFoundException(Person.class, "id", id);
        }

        return entity;
    }

    @SuppressWarnings("unchecked")
    default List<E> searchByParams(Map<String, Object> params) throws HibernateException {
        return executeQuery((Session session) -> {

            StringBuilder query = new StringBuilder();

            String entityClassName = getEntityClass().getSimpleName();
            query.append("from ").append(entityClassName);

            int index = 0;
            for (String key : params.keySet()) {

                String property = StringUtils.trimToEmpty(key);
                Object value = params.get(key);
                if (value instanceof String) {
                    value = StringUtils.trimToNull((String)value);
                }

                if (value == null) continue;

                if (index == 0) {
                    query.append(" e where");
                } else {
                    query.append(" and ");
                }

                if (value instanceof String) {
                    query.append(" lower(e.").append(property).append(") like :").append(property);
                } else {
                    query.append(" e.").append(property).append("=:").append(property);
                }

                index++;
            }

            Query hibQuery = session.createQuery(query.toString());

            for (String key : params.keySet()) {

                String property = StringUtils.trimToEmpty(key);
                Object value = params.get(key);
                if (value instanceof String) {
                    value = StringUtils.trimToNull((String)value);
                }

                if (value == null) continue;

                if (value instanceof String) {
                    hibQuery.setParameter(property, "%" + value + "%");
                } else {
                    hibQuery.setParameter(property, value);
                }
            }

            getLogger().log(Level.FINE, String.format("Query for %s constructed %s", getEntityClass(), hibQuery.getQueryString()));

            return hibQuery.list();
        });
    }
}
