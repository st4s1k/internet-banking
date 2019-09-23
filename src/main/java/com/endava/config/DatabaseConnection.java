package com.endava.config;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

@Component
public class DatabaseConnection {

    public void initDB() {
        StringBuilder query = new StringBuilder();

        query.append("create table users (");
        query.append("   id serial primary key,");
        query.append("   name varchar(20) not null");
        query.append(");");

        query.append("create table accounts (");
        query.append("   id serial primary key,");
        query.append("   funds numeric not null,");
        query.append("   user_id integer references users(id) on delete cascade");
        query.append(");");

        transaction(statement -> {
            try {
                statement.executeQuery(query.toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        });
    }

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
