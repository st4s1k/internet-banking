package com.endava.internship.internetbanking.repositories;

import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.AccountSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyList;
import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

@Repository
public class AccountSnapshotRepository {

    private final AccountRepository accountRepository;

    private final EntityManager entityManager;

    @Autowired
    public AccountSnapshotRepository(AccountRepository accountRepository,
                                     EntityManager entityManager) {
        this.accountRepository = accountRepository;
        this.entityManager = entityManager;
    }

    public Optional<AccountSnapshot> find(AccountSnapshot accountSnapshot) {
        return Optional.ofNullable(accountSnapshot)
                .flatMap(t -> Optional.ofNullable(t.getId()))
                .flatMap(this::findById);
    }

    public Optional<AccountSnapshot> findById(Long accountSnapshotId) {
        return Optional.ofNullable(entityManager.find(AccountSnapshot.class, accountSnapshotId));
    }

    public List<AccountSnapshot> findAll() {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountSnapshot> query = cb.createQuery(AccountSnapshot.class);
        Root<AccountSnapshot> snapshotFields = query.from(AccountSnapshot.class);

        query.select(snapshotFields);

        return entityManager.createQuery(query).getResultList();
    }

    public List<AccountSnapshot> findByAccount(Account account) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountSnapshot> query = cb.createQuery(AccountSnapshot.class);
        Root<AccountSnapshot> snapshotFields = query.from(AccountSnapshot.class);

        query.select(snapshotFields)
                .where(cb.equal(snapshotFields.get("account"), account));

        return entityManager.createQuery(query).getResultList();
    }

    public List<AccountSnapshot> findByAccountId(Long accountId) {
        return Optional.ofNullable(accountId)
                .flatMap(accountRepository::findById)
                .map(this::findByAccount)
                .orElse(emptyList());
    }

    public AccountSnapshot findLatestBefore(Account account, LocalDateTime dateTime) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountSnapshot> query = cb.createQuery(AccountSnapshot.class);
        Root<AccountSnapshot> snapshotFields = query.from(AccountSnapshot.class);

        query.select(snapshotFields)
                .where(cb.equal(snapshotFields.get("account"), account))
                .groupBy(snapshotFields.get("id"),
                        snapshotFields.get("account"),
                        snapshotFields.get("funds"))
                .having(cb.lessThanOrEqualTo(cb.greatest(
                        snapshotFields.get("dateTime").as(LocalDateTime.class)), dateTime))
                .orderBy(cb.desc(snapshotFields.get("dateTime")));

        return entityManager.createQuery(query).getSingleResult();
    }

    public List<AccountSnapshot> findAllLatestBefore(Set<Account> accounts, LocalDateTime dateTime) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountSnapshot> query = cb.createQuery(AccountSnapshot.class);
        Root<AccountSnapshot> snapshotFields = query.from(AccountSnapshot.class);

        query.select(snapshotFields)
                .where(snapshotFields.get("account").in(accounts))
                .groupBy(snapshotFields.get("id"),
                        snapshotFields.get("account"),
                        snapshotFields.get("funds"))
                .having(cb.lessThanOrEqualTo(cb.greatest(
                        snapshotFields.get("dateTime").as(LocalDateTime.class)), dateTime))
                .orderBy(cb.desc(snapshotFields.get("dateTime")));

        return entityManager.createQuery(query).getResultList();
    }

    public AccountSnapshot findEarliestAfter(Account account, LocalDateTime dateTime) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountSnapshot> query = cb.createQuery(AccountSnapshot.class);
        Root<AccountSnapshot> snapshotFields = query.from(AccountSnapshot.class);

        query.select(snapshotFields)
                .where(cb.equal(snapshotFields.get("account"), account))
                .groupBy(snapshotFields.get("id"),
                        snapshotFields.get("account"),
                        snapshotFields.get("funds"))
                .having(cb.greaterThanOrEqualTo(cb.least(
                        snapshotFields.get("dateTime").as(LocalDateTime.class)), dateTime))
                .orderBy(cb.desc(snapshotFields.get("dateTime")));

        return entityManager.createQuery(query).getSingleResult();
    }

    public List<AccountSnapshot> findAllEarliestAfter(Set<Account> accounts, LocalDateTime dateTime) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountSnapshot> query = cb.createQuery(AccountSnapshot.class);
        Root<AccountSnapshot> snapshotFields = query.from(AccountSnapshot.class);

        query.select(snapshotFields)
                .where(snapshotFields.get("account").in(accounts))
                .groupBy(snapshotFields.get("id"),
                        snapshotFields.get("account"),
                        snapshotFields.get("funds"))
                .having(cb.greaterThanOrEqualTo(cb.least(
                        snapshotFields.get("dateTime").as(LocalDateTime.class)), dateTime))
                .orderBy(cb.desc(snapshotFields.get("dateTime")));

        return entityManager.createQuery(query).getResultList();
    }
}
