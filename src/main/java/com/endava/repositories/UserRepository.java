package com.endava.repositories;

import com.endava.config.DatabaseConnection;
import com.endava.entities.User;
import com.endava.sevices.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
// TODO: write integration test, with full spring context using h2 db
public class UserRepository implements Repository<User> {

    @Autowired
    private DatabaseConnection dbConnection;

    @Autowired
    private AccountService accountService;

    @Override
    public List<User> findAll() {

        return dbConnection.transaction(statement -> {

            List<User> users = new LinkedList<>();

            try (ResultSet resultSet = statement.executeQuery("select * from users")) {
                users.add(getUserObject(resultSet));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return Optional.of(users);

        }).orElse(Collections.emptyList());
    }

    @Override
    public Optional<User> findById(Long id) {
        return findByField("id", id);
    }

    public Optional<User> findByName(String name) {
        return findByField("name", "'" + name + "'");
    }

    public Optional<User> findByField(String field, Object value) {
        return Optional.ofNullable(field)
                .flatMap((_field) -> dbConnection.transaction(statement -> {
                    Optional<User> user = Optional.empty();
                    try (ResultSet resultSet = statement.executeQuery("select * from users where " + _field + " = " + value)) {
                        user = Optional.ofNullable(getUserObject(resultSet));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return user;
                }));
    }

    @Override
    public boolean save(User user) throws SQLException {

        if (!Optional.ofNullable(user).isPresent()) {
            throw new SQLException("Attempt to save a null entry!");
        }

        Optional<User> sameUser = findByName(user.getName());

        if (sameUser.isPresent()) {
            throw new SQLException("Attempt to add an existing user (id: " + sameUser.get().getId() + ").");
        }

        String query = "insert into users(name) values ('" + user.getName() + "');";

        dbConnection.transaction(statement -> {
            try {
                statement.executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        });

        return user.getId() == null ? true : findById(user.getId())
                .map(foundUser -> foundUser.equals(user))
                .orElse(false);

    }

    @Override
    public boolean remove(User user) {
        return false;
    }

    private User getUserObject(ResultSet resultSet) throws SQLException {
        User user = null;
        while (resultSet.next()) {
            user = new User.Builder()
                    .setId(resultSet.getLong(1))
                    .setName(resultSet.getString(2))
                    .build();
        }
        return user;
    }
}
