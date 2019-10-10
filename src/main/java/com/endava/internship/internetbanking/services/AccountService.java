package com.endava.internship.internetbanking.services;

import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.User;
import com.endava.internship.internetbanking.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> createAccount(@NonNull User user) {
        return accountRepository.save(new Account(user));
    }

    public Optional<Account> update(@NonNull Account account) {
        return accountRepository.update(account);
    }

    public Optional<Account> remove(@NonNull Account account) {
        return accountRepository.remove(account);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(@NonNull Long id) {
        return accountRepository.findById(id);
    }

    public List<Account> findByUser(@NonNull User user) {
        return accountRepository.findByUser(user);
    }

    public List<Account> findByUserId(@NonNull Long id) {
        return accountRepository.findByUserId(id);
    }
}
