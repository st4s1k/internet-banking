package com.endava.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;
import java.util.function.Function;

@Component
public class DatabaseConnection {

    @Autowired
    private DataSource dataSource;

    public <T> Optional<T> transaction(Function<Statement, Optional<T>> operation) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            return operation.apply(statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
