package com.endava.entities;

import com.endava.annotations.Column;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public interface Entity {

    String getTableName();

    String getIdName();

    Object getIdValue();

    default String[] getFieldNames() {
        return Arrays.stream(getColumns())
                .map(field -> field.getAnnotation(Column.class).value())
                .toArray(String[]::new);
    }

    default Object[] getFieldValues() {
        return Arrays.stream(getColumns())
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        return field.get(this);
                    } catch (IllegalAccessException e) {
                        return null;
                    }
                }).toArray();
    }

    default Field[] getColumns() {
        return Arrays.stream(getClass().getDeclaredFields())
                .filter(field -> Optional.ofNullable(field.getAnnotation(Column.class))
                        .isPresent())
                .toArray(Field[]::new);
    }
}
