package com.endava.internship.internetbanking.sevices;

import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.User;
import com.endava.internship.internetbanking.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    public List<Account> findByUser(User user) {
        return accountRepository.findByUser(user);
    }

    public Optional<Account> update(Account account) {
        return accountRepository.update(account);
    }

    public Optional<Account> createAccount(User user) {
        return accountRepository.save(Account.builder()
                .setUser(user)
                .build());
    }
}
