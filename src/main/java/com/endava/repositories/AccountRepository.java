package com.endava.repositories;

import com.endava.config.DatabaseConnection;
import com.endava.entities.Account;
import com.endava.entities.User;
import com.endava.sevices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AccountRepository implements Repository<Account> {

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseConnection dbConnection;

    @Override
    public DatabaseConnection getDatabaseConnection() {
        return dbConnection;
    }

    @Override
    public String getTableName() {
        return Account.TABLE_NAME;
    }

    @Override
    public String getIdName() {
        return Account.ID_NAME;
    }

    @Override
    public Class<Account> getEntityClass() {
        return Account.class;
    }

    public Optional<Account> findByUser(User user) {
        List<Account> accounts = findByField("user_id", user.getId());
        return accounts.isEmpty() ? Optional.empty() : Optional.ofNullable(accounts.get(0));
    }
}
