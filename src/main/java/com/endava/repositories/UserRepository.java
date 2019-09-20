package com.endava.repositories;

import com.endava.config.DatabaseConnection;
import com.endava.entities.Account;
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
public class UserRepository implements Repository<User> {

    @Autowired
    private DatabaseConnection dbConnection;

    @Autowired
    private AccountService accountService;

    @Override
    public List<User> findAll() {

        return dbConnection.transaction(statement -> {

            List<User> users = new LinkedList<>();

            try {
                ResultSet resultSet = statement.executeQuery("select * from users");
                while (resultSet.next()) {
                    users.add(getUserObject(resultSet));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return Optional.of(users);

        }).orElse(Collections.emptyList());
    }

    @Override
    public Optional<User> findById(Long id) {

        return dbConnection.transaction(statement -> {

            Optional<User> user = Optional.empty();

            try {
                ResultSet resultSet = statement.executeQuery("select * form users where id = " + id);
                user = Optional.of(getUserObject(resultSet));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return user;
        });
    }

    @Override
    public boolean save(User user) throws SQLException {

        String query = findById(user.getId()).map(foundUser ->
                "update users " +
                        "set name = " + user.getName() + "," +
                        "account_id = " + user.getAccount().getId() +
                        "where id = " + foundUser.getId() + ";")
                .orElseThrow(() ->
                        new SQLException("Attempt to add an existing user (id: " + user.getId() + ")."));

        dbConnection.transaction(statement -> {
            try {
                statement.executeQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        });

        return findById(user.getId())
                .map(foundUser -> foundUser.equals(user))
                .orElse(false);
    }

    @Override
    public boolean remove(User user) {
        return false;
    }

    private User getUserObject(ResultSet resultSet) throws SQLException {

        Long id = resultSet.getLong(1);
        String name = resultSet.getString(2);
        Account account = accountService
                .findById(resultSet.getLong(3))
                .orElse(null);

        return new User.Builder()
                .setId(id)
                .setName(name)
                .setAccount(account)
                .build();
    }
}
