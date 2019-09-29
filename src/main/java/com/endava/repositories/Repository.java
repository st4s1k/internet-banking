package com.endava.repositories;

import com.endava.config.DatabaseConnection;
import com.endava.entities.Entity;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

interface Repository<T extends Entity> {

    Function<Entity, String> REMOVE_SQL = e -> {
        String table = e.getTableName();
        String column = e.getIdName();
        String value = e.getIdValue().toString();
        return "delete from " + table + " where " + column + " = " + value + " returning *";
    };

    Function<Entity, String> INSERT_SQL = e -> {
        String table = e.getTableName();
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        String[] fieldNames = e.getFieldNames();
        Object[] fieldValues = e.getFieldValues();
        for (int i = 0; i < fieldNames.length; i++) {
            if (Optional.ofNullable(fieldValues[i]).isPresent()) {
                if (columns.length() > 0) {
                    columns.append(", ");
                    values.append(", ");
                }
                columns.append(fieldNames[i]);
                values.append(fieldValues[i]);
            }
        }
        return "insert into " + table + "(" + columns + ") values ('" + values + "') returning *";
    };

    Function<Entity, String> UPDATE_SQL = e -> {
        String table = e.getTableName();
        String[] fieldNames = e.getFieldNames();
        Object[] fieldValues = e.getFieldValues();
        StringBuilder columns = new StringBuilder();
        for (int i = 0; i < fieldNames.length; i++) {
            if (columns.length() > 0) {
                columns.append(", ");
            }
            columns.append(fieldNames[i]).append(" = ").append(fieldValues[i]);
        }
        return columns.length() == 0 ? ""
                : "update " + table + " set " + columns + " returning *";
    };

    Function<String, String> SELECT_ALL_SQL = table -> "select * from " + table;

    Function<Object[], String> SELECT_ALL_BY_FIELD_SQL = a -> SELECT_ALL_SQL.apply("" + a[0]) +
            " where " + a[1] + " = " + a[2];

    DatabaseConnection getDatabaseConnection();

    String getTableName();

    String getIdName();

    default Optional<T> remove(T entity) {
        return findById(entity.getIdValue())
                .flatMap(_entity -> getDatabaseConnection()
                        .transaction(statement -> {
                            try (ResultSet resultSet = statement.executeQuery(REMOVE_SQL.apply(_entity))) {
                                return Optional.of(getObjects(resultSet).get(0));
                            } catch (SQLException e) {
                                e.printStackTrace();
                                return Optional.empty();
                            }
                        }));
    }

    default Optional<T> save(T entity) {
        return getDatabaseConnection()
                .transaction(statement -> {
                    try (ResultSet resultSet = statement.executeQuery(INSERT_SQL.apply(entity))) {
                        return Optional.of(getObjects(resultSet).get(0));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return Optional.empty();
                    }
                });
    }

    default Optional<T> getObject(ResultSet resultSet) throws SQLException {
        try {
            Class<T> entityClass = getEntityClass();
            T entityInstance = entityClass.newInstance();
            Field[] fields = entityInstance.getColumns();
            if (resultSet.getMetaData().getColumnCount() != fields.length) {
                return Optional.empty();
            }
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                field.set(entityInstance, resultSet.getObject(i + 1, field.getType()));
            }
            return Optional.of(entityInstance);
        } catch (InstantiationException
                | IllegalAccessException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


    default Optional<T> update(T entity) {
        return findById(entity.getIdValue())
                .flatMap(_entity -> getDatabaseConnection()
                        .transaction(statement -> {
                            try (ResultSet resultSet = statement.executeQuery(UPDATE_SQL.apply(_entity))) {
                                return Optional.of(getObjects(resultSet).get(0));
                            } catch (SQLException e) {
                                e.printStackTrace();
                                return Optional.empty();
                            }
                        }));
    }

    Class<T> getEntityClass();

    default List<T> getObjects(ResultSet resultSet) throws SQLException {
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            getObject(resultSet).ifPresent(list::add);
        }
        return list;
    }

    default Optional<T> findById(Object id) {
        return Optional.ofNullable(findByField(getIdName(), id).get(0));
    }

    default List<T> findAll() {
        return getDatabaseConnection()
                .transaction(statement -> {
                    try (ResultSet resultSet = statement.executeQuery(SELECT_ALL_SQL.apply(getTableName()))) {
                        return Optional.of(getObjects(resultSet));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return Optional.empty();
                    }
                }).orElse(Collections.emptyList());
    }

    default List<T> findByField(String field, Object value) {
        String _value = value instanceof String ? "'" + value + "'" : value.toString();
        return Optional.ofNullable(field)
                .flatMap(_field -> getDatabaseConnection().transaction(statement -> {
                    try (ResultSet resultSet = statement.executeQuery(SELECT_ALL_BY_FIELD_SQL
                            .apply(new Object[]{getTableName(), _field, _value}))) {
                        return Optional.of(getObjects(resultSet));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return Optional.empty();
                    }
                })).orElse(Collections.emptyList());
    }
}
