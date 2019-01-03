package com.sheva.db;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used for extending default list of Hibernate types in order to be conform with Java 8 {@link java.time.LocalDate} recommended class.
 *
 * Created by Sheva on 10/1/2016.
 */
public class LocalDateType implements UserType {

    private static final Logger logger = Logger.getLogger(LocalDateType.class.getName());

    private static final String DATE_FORMAT = PropertiesFileResolver.INSTANCE.getDatabaseDateFormat();

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };
    }

    @Override
    public Class returnedClass() {
        return LocalDate.class;
    }

    @Override
    public boolean equals(Object o, Object o1) throws HibernateException {
        return (o == null) ? (o1 == null) : o.equals(o1);
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return o.hashCode();
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] strings,
                              SharedSessionContractImplementor implementor, Object o)
            throws HibernateException, SQLException {
        if (!resultSet.wasNull()) {
            String dateString = resultSet.getString(strings[0]);

            if (dateString != null) {
                LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_FORMAT));
                logger.log(Level.FINEST, "Get date " + dateString);
                return date;
            }
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement statement, Object value, int index,
                            SharedSessionContractImplementor implementor)
            throws HibernateException, SQLException {
        if (value == null) {
            statement.setNull(index, Types.VARCHAR);
            logger.log(Level.FINEST, "Date set to null.");
        } else {
            String date = ((LocalDate) value).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            statement.setString(index, date);
            logger.log(Level.FINEST, "Date set to " + date);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return (value == null) ? null : (LocalDate) value;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }
}
