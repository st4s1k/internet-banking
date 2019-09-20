package com.endava.repositories;

import com.endava.config.DatabaseConnection;
import com.endava.entities.Account;
import com.endava.entities.User;
import com.endava.sevices.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Component
public class UserRepository implements Repository<User> {

    @Autowired
    private DatabaseConnection dbConnection;

    @Autowired
    private AccountService accountService;

    @Override
    public List<User> findAll() {

        return dbConnection.transaction(connection -> {

            List<User> users = new LinkedList<>();

            try {
                Statement statement = connection.createStatement();
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

        return dbConnection.transaction(connection -> {

            Optional<User> user = Optional.empty();

            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select * form users where id = " + id);
                user = Optional.of(getUserObject(resultSet));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return user;
        });
    }

    @Override
    public boolean save(User user) {

        String query = findById(user.getId()).map(foundUser ->
                "update users " +
                        "set name = " + user.getName() + "," +
                        "account_id = " + user.getAccount().getId() +
                        "where id = " + foundUser.getId() + ";")
                .orElse("insert into users (id, name, account_id) values (" +
                        user.getId() + "," +
                        user.getName() + "," +
                        user.getAccount().getId() + ");");

        dbConnection.transaction(connection -> {
            try {
                Statement statement = connection.createStatement();
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
