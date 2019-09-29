package com.endava.repositories;

import com.endava.config.DatabaseConnection;
import com.endava.entities.User;
import com.endava.sevices.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public DatabaseConnection getDatabaseConnection() {
        return dbConnection;
    }

    @Override
    public String getTableName() {
        return User.TABLE_NAME;
    }

    @Override
    public String getIdName() {
        return User.ID_NAME;
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    public Optional<User> findByName(String name) {
        List<User> users = findByField("name", name);
        return users.isEmpty() ? Optional.empty() : Optional.ofNullable(users.get(0));
    }
}
