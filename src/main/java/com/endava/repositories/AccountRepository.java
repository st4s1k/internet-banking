package com.endava.repositories;

import com.endava.entities.Account;
import com.endava.entities.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class AccountRepository implements Repository<Account> {

    @Override
    public List<Account> findAll() {
        return Collections.emptyList();
    }

    @Override
    public Optional<Account> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public boolean save(Account acc) {
        return false;
    }

    @Override
    public boolean remove(Account o) {
        return false;
    }

    public Optional<Account> findByUser(User user) {
        return Optional.empty();
    }
}
