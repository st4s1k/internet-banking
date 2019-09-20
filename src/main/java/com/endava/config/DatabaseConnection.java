package com.endava.config;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

@Component
public class DatabaseConnection {

    public <T> Optional<T> transaction(Function<Statement, Optional<T>> operation) {

        String url = "jdbc:postgresql:internetbanking";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");

        try (Connection connection = DriverManager.getConnection(url, props);
             Statement statement = connection.createStatement()) {
            Class.forName("org.postgresql.Driver");
            return operation.apply(statement);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
