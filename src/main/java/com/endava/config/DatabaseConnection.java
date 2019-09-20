package com.endava.config;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

@Component
public class DatabaseConnection {

    public <T> Optional<T> transaction(Function<Connection, Optional<T>> operation) {

        String url = "jdbc:postgresql:internetbanking";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");

        try (Connection connection = DriverManager.getConnection(url, props)) {
            Class.forName("org.postgresql.Driver");
            return operation.apply(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
