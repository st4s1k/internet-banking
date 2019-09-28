package com.endava.config;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

@Component
public class DatabaseConnection {

    @Autowired
    private DataSource dataSource;

    public void initDB() {
        // TODO: Refactor using flyway
        transaction(statement -> {
            crateTable(statement, "users",
                    "id serial primary key",
                    "name varchar(20) not null");
            crateTable(statement, "accounts",
                    "id serial primary key",
                    "funds numeric not null",
                    "user_id integer references users(id) on delete cascade");
            return Optional.empty();
        });
    }

    private void crateTable(Statement statement, String table, String... columns) {
        try (ResultSet resultSet = statement.executeQuery("select to_regclass('" + table + "')")) {
            while (resultSet.next()) {
                if (resultSet.getString(1) != null) {
                    return;
                }
            }
            String _columns = StringUtils.join(Arrays.asList(columns), ',');
            statement.executeUpdate("create table " + table + " (" + _columns + ");");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <T> Optional<T> transaction(Function<Statement, Optional<T>> operation) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            Class.forName("org.postgresql.Driver");
            return operation.apply(statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
