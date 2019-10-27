package com.endava.internship.internetbanking.services;

import com.endava.internship.internetbanking.dto.AccountDTO;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.User;
import com.endava.internship.internetbanking.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

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

    @Transactional(REQUIRES_NEW)
    public Optional<Account> createAccount(User user) {
        return accountRepository.save(new Account(user));
    }

    @Transactional(REQUIRES_NEW)
    public Optional<Account> createAccount(Account account) {
        return accountRepository.save(account);
    }

    @Transactional(REQUIRES_NEW)
    public Optional<Account> update(Account account) {
        return accountRepository.update(account);
    }

    @Transactional(REQUIRES_NEW)
    public Optional<Account> remove(Account account) {
        return accountRepository.remove(account);
    }

    @Transactional(REQUIRES_NEW)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Transactional(REQUIRES_NEW)
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    @Transactional(REQUIRES_NEW)
    public List<Account> findByUser(User user) {
        return accountRepository.findByUser(user);
    }

    @Transactional(REQUIRES_NEW)
    public List<Account> findByUserId(Long id) {
        return accountRepository.findByUserId(id);
    }

    @Transactional(REQUIRES_NEW)
    public Account accountFromDTO(@NotNull AccountDTO dto) {
        return accountRepository.findById(dto.getId())
                .orElseGet(() -> {
                    Account.AccountBuilder builder = Account.builder()
                            .id(dto.getId())
                            .funds(dto.getFunds());
                    userService.findById(dto.getUserId()).ifPresent(builder::user);
                    return builder.build();
                });
    }
}
