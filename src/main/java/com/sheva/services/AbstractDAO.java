package com.sheva.services;

import com.sheva.api.exceptions.AlreadyExistsException;
import com.sheva.api.exceptions.EntityNotFoundException;
import com.sheva.api.exceptions.InvalidRequestDataException;
import com.sheva.db.Database;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.sheva.utils.ApplicationHelper.getEntityClass;

/**
 * Base DAO class for manipulating with persistent entities.
 *
 * Created by Sheva on 10/2/2016.
 */
public abstract class AbstractDAO<E> {

    private static final Logger logger = Logger.getLogger(FoodDAO.class.getName());

    private final Class entityClass;
    private final SessionFactory sessionFactory;

    AbstractDAO() {
        entityClass = getEntityClass(this);
        sessionFactory = Database.INSTANCE.getFactory();
    }

    abstract E create(E entity) throws HibernateException;
    abstract E update(E entity) throws HibernateException;
    abstract void delete(E entity) throws HibernateException;

    interface ExecutableQuery<E> {
        E execute(Session session) throws HibernateException;
    }

    <T> T executeQuery(ExecutableQuery<T> query) throws HibernateException {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            T result = query.execute(session);
            transaction.commit();
            return result;
        } catch (PersistenceException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException constraint = (ConstraintViolationException) e.getCause();
                final String constraintName = constraint.getConstraintName();
                if  (constraintName.contains("UNIQUE")) {
                    logger.log(Level.WARNING, e.toString(), e);
                    throw new AlreadyExistsException(entityClass, e.getCause());
                } else if (constraintName.contains("NOT NULL")) {
                    logger.log(Level.WARNING, e.toString(), e);
                    SQLException sqlException = ((ConstraintViolationException) e.getCause()).getSQLException();
                    throw new InvalidRequestDataException(entityClass.getSimpleName(), null, null, sqlException);
                } else {
                    throw e;
                }
            }

            logger.log(Level.SEVERE, e.toString(), e);
            throw e;
        }
    }

    @SuppressWarnings(value = "unchecked")
    public List<E> findAll() throws WebApplicationException {
        return executeQuery((Session session) -> session.createQuery("from " + entityClass.getSimpleName()).list());
    }

    @SuppressWarnings(value = "unchecked")
    public E findById(final int id) throws WebApplicationException {
        String query = String.format("from %s e where e.id=:id", entityClass.getSimpleName());
        E entity = executeQuery((Session session) -> (E) session.createQuery(query).setParameter("id", id).uniqueResult());

        if (entity == null) {
            logger.log(Level.WARNING, String.format("Entity %s was not found by id:%d.", entityClass, id));
            throw new EntityNotFoundException(entityClass, "id", id);
        }

        return entity;
    }

    @SuppressWarnings("unchecked")
    public List<E> searchByParams(Map<String, Object> params) throws HibernateException {
        return executeQuery((Session session) -> {
            StringBuilder query = new StringBuilder();
            String entityClassName = entityClass.getSimpleName();
            query.append("from ").append(entityClassName);

            final AtomicInteger index = new AtomicInteger(0);
            params.forEach((property, value) -> {
                if (value instanceof String) {
                    value = StringUtils.trimToNull((String)value);
                }
                if (value == null) {
                    return;
                }

                if (index.getAndIncrement() == 0) {
                    query.append(" e where ");
                } else {
                    query.append(" and ");
                }

                final String propertyQueryPart;
                if (value instanceof String) {
                    propertyQueryPart = "lower(e." + property + ") like :" + property;
                } else {
                    propertyQueryPart = "e." + property + "=:" + property;
                }
                query.append(propertyQueryPart);
            });

            Query hibQuery = session.createQuery(query.toString());

            params.forEach((property, value) -> {
                if (value instanceof String) {
                    value = StringUtils.trimToNull((String) value);
                }
                if (value == null) {
                    return;
                }

                hibQuery.setParameter(property, (value instanceof String) ? "%" + value + "%" : value);
            });

            logger.log(Level.FINEST, String.format("Query for %s constructed %s", entityClass, hibQuery.getQueryString()));

            return hibQuery.list();
        });
    }
}
