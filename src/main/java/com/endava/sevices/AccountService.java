package com.endava.sevices;

import com.endava.entities.Account;
import com.endava.entities.User;
import com.endava.repositories.AccountRepository;
import com.endava.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    public Optional<Account> findByUser(User user) {
        return accountRepository.findByUser(user);
    }

    public Optional<Account> update(Account account) {
        return accountRepository.update(account);
    }

    public Optional<Account> createAccount(User user) {
        return accountRepository.save(new Account.Builder()
                .setUser(user)
                .build());
    }
}
