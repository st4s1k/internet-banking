package com.endava.internship.internetbanking.services;

import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.AccountSnapshot;
import com.endava.internship.internetbanking.repositories.AccountSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AccountSnapshotService {

    private final AccountSnapshotRepository accountSnapshotRepository;

    @Autowired
    public AccountSnapshotService(AccountSnapshotRepository accountSnapshotRepository) {
        this.accountSnapshotRepository = accountSnapshotRepository;
    }

    public Optional<AccountSnapshot> find(AccountSnapshot accountSnapshot) {
        return accountSnapshotRepository.find(accountSnapshot);
    }

    public Optional<AccountSnapshot> findById(Long accountSnapshotId) {
        return accountSnapshotRepository.findById(accountSnapshotId);
    }

    public List<AccountSnapshot> findAll() {
        return accountSnapshotRepository.findAll();
    }

    public List<AccountSnapshot> findByAccountId(Long accountId) {
        return accountSnapshotRepository.findByAccountId(accountId);
    }

    public List<AccountSnapshot> findByAccount(Account account) {
        return accountSnapshotRepository.findByAccount(account);
    }

    public AccountSnapshot findLatestBefore(Long accountId, LocalDateTime dateTime) {
        return accountSnapshotRepository.findLatestBefore(accountId, dateTime);
    }

    public AccountSnapshot findEarliestAfter(Long accountId, LocalDateTime dateTime) {
        return accountSnapshotRepository.findEarliestAfter(accountId, dateTime);
    }

    public List<AccountSnapshot> findAllLatestBefore(Set<Account> accounts, LocalDateTime dateTime) {
        return accountSnapshotRepository.findAllLatestBefore(accounts, dateTime);
    }

    public List<AccountSnapshot> findAllEarliestAfter(Set<Account> accounts, LocalDateTime dateTime) {
        return accountSnapshotRepository.findAllEarliestAfter(accounts, dateTime);
    }

}
