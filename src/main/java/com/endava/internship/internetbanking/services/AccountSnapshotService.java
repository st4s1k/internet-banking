package com.endava.internship.internetbanking.services;

import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.AccountSnapshot;
import com.endava.internship.internetbanking.repositories.AccountSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
public class AccountSnapshotService {

    private final AccountSnapshotRepository accountSnapshotRepository;

    @Autowired
    public AccountSnapshotService(AccountSnapshotRepository accountSnapshotRepository) {
        this.accountSnapshotRepository = accountSnapshotRepository;
    }

    @Transactional(propagation = REQUIRES_NEW)
    public Optional<AccountSnapshot> find(AccountSnapshot accountSnapshot) {
        return accountSnapshotRepository.find(accountSnapshot);
    }

    @Transactional(propagation = REQUIRES_NEW)
    public Optional<AccountSnapshot> findById(Long accountSnapshotId) {
        return accountSnapshotRepository.findById(accountSnapshotId);
    }

    @Transactional(propagation = REQUIRES_NEW)
    public List<AccountSnapshot> findAll() {
        return accountSnapshotRepository.findAll();
    }

    @Transactional(propagation = REQUIRES_NEW)
    public List<AccountSnapshot> findByAccountId(Long accountId) {
        return accountSnapshotRepository.findByAccountId(accountId);
    }

    @Transactional(propagation = REQUIRES_NEW)
    public List<AccountSnapshot> findByAccount(Account account) {
        return accountSnapshotRepository.findByAccount(account);
    }

    @Transactional(propagation = REQUIRES_NEW)
    public AccountSnapshot findLatestBefore(Account account, LocalDateTime dateTime) {
        return accountSnapshotRepository.findLatestBefore(account, dateTime);
    }

    @Transactional(propagation = REQUIRES_NEW)
    public AccountSnapshot findEarliestAfter(Account account, LocalDateTime dateTime) {
        return accountSnapshotRepository.findEarliestAfter(account, dateTime);
    }

    @Transactional(propagation = REQUIRES_NEW)
    public List<AccountSnapshot> findAllLatestBefore(Set<Account> accounts, LocalDateTime dateTime) {
        return accountSnapshotRepository.findAllLatestBefore(accounts, dateTime);
    }

    @Transactional(propagation = REQUIRES_NEW)
    public List<AccountSnapshot> findAllEarliestAfter(Set<Account> accounts, LocalDateTime dateTime) {
        return accountSnapshotRepository.findAllEarliestAfter(accounts, dateTime);
    }

}
