package com.endava.internship.internetbanking.services;

import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.AccountSnapshot;
import com.endava.internship.internetbanking.repositories.AccountSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

@Service
public class AccountSnapshotService {

    private final AccountSnapshotRepository accountSnapshotRepository;

    @Autowired
    public AccountSnapshotService(AccountSnapshotRepository accountSnapshotRepository) {
        this.accountSnapshotRepository = accountSnapshotRepository;
    }

    @Transactional(REQUIRES_NEW)
    public Optional<AccountSnapshot> find(AccountSnapshot accountSnapshot) {
        return accountSnapshotRepository.find(accountSnapshot);
    }

    @Transactional(REQUIRES_NEW)
    public Optional<AccountSnapshot> findById(Long accountSnapshotId) {
        return accountSnapshotRepository.findById(accountSnapshotId);
    }

    @Transactional(REQUIRES_NEW)
    public List<AccountSnapshot> findAll() {
        return accountSnapshotRepository.findAll();
    }

    @Transactional(REQUIRES_NEW)
    public List<AccountSnapshot> findByAccountId(Long accountId) {
        return accountSnapshotRepository.findByAccountId(accountId);
    }

    @Transactional(REQUIRES_NEW)
    public List<AccountSnapshot> findByAccount(Account account) {
        return accountSnapshotRepository.findByAccount(account);
    }

    @Transactional(REQUIRES_NEW)
    public AccountSnapshot findLatestBefore(Account account, LocalDateTime dateTime) {
        return accountSnapshotRepository.findLatestBefore(account, dateTime);
    }

    @Transactional(REQUIRES_NEW)
    public AccountSnapshot findEarliestAfter(Account account, LocalDateTime dateTime) {
        return accountSnapshotRepository.findEarliestAfter(account, dateTime);
    }

    @Transactional(REQUIRES_NEW)
    public List<AccountSnapshot> findAllLatestBefore(Set<Account> accounts, LocalDateTime dateTime) {
        return accountSnapshotRepository.findAllLatestBefore(accounts, dateTime);
    }

    @Transactional(REQUIRES_NEW)
    public List<AccountSnapshot> findAllEarliestAfter(Set<Account> accounts, LocalDateTime dateTime) {
        return accountSnapshotRepository.findAllEarliestAfter(accounts, dateTime);
    }

}
