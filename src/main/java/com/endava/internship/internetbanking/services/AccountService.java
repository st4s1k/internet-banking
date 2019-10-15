package com.endava.internship.internetbanking.services;

import com.endava.internship.internetbanking.dto.AccountDTO;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.User;
import com.endava.internship.internetbanking.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    public Optional<Account> createAccount(User user) {
        return accountRepository.save(new Account(user));
    }

    public Optional<Account> update(Account account) {
        return accountRepository.update(account);
    }

    public Optional<Account> remove(Account account) {
        return accountRepository.remove(account);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    public List<Account> findByUser(User user) {
        return accountRepository.findByUser(user);
    }

    public List<Account> findByUserId(Long id) {
        return accountRepository.findByUserId(id);
    }

    public Account accountFromDTO(AccountDTO accountDTO) {
        Account.AccountBuilder accountBuilder = Account.builder()
                .id(accountDTO.getId())
                .funds(accountDTO.getFunds());
        Optional<User> optUser = userService.findById(accountDTO.getUserId());
        optUser.ifPresent(accountBuilder::user);
        return accountBuilder.build();
    }
}
