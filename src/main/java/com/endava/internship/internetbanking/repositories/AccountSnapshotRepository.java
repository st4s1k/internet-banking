package com.endava.internship.internetbanking.repositories;

import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.AccountSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyList;
import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

@Repository
@Transactional(REQUIRES_NEW)
public class AccountSnapshotRepository {

    private final EntityManager entityManager;

    @Autowired
    public AccountSnapshotRepository(EntityManager entityManager) {
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

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountSnapshot> query = criteriaBuilder.createQuery(AccountSnapshot.class);
        Root<AccountSnapshot> from = query.from(AccountSnapshot.class);

        query.select(from);
        return entityManager.createQuery(query).getResultList();
    }

    public List<AccountSnapshot> findByAccountId(Long accountId) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountSnapshot> query = criteriaBuilder.createQuery(AccountSnapshot.class);
        Root<AccountSnapshot> from = query.from(AccountSnapshot.class);

        Predicate accountIdCriteria = criteriaBuilder.equal(from.get("account"), accountId);

        query.where(accountIdCriteria);
        return entityManager.createQuery(query).getResultList();
    }

    public List<AccountSnapshot> findByAccount(Account account) {

        return Optional.ofNullable(account)
                .flatMap(a -> Optional.ofNullable(a.getId()))
                .map(this::findByAccountId)
                .orElse(emptyList());
    }

    public AccountSnapshot findLatestBefore(Long accountId, LocalDateTime dateTime) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountSnapshot> query = criteriaBuilder.createQuery(AccountSnapshot.class);
        Root<AccountSnapshot> from = query.from(AccountSnapshot.class);

        Subquery<LocalDateTime> subQuery = query.subquery(LocalDateTime.class);
        Root<AccountSnapshot> fromSubQuery = subQuery.from(AccountSnapshot.class);
        subQuery.select(criteriaBuilder.greatest(fromSubQuery.<LocalDateTime>get("dateTime")));

        Predicate queryCriteria = criteriaBuilder.and(
                criteriaBuilder.equal(from.get("account"), accountId),
                criteriaBuilder.equal(from.get("dateTime"), subQuery));

        query.where(queryCriteria).orderBy(criteriaBuilder.desc(from.get("dateTime")));
        return entityManager.createQuery(query)
                .setFirstResult(0)
                .setMaxResults(1)
                .getSingleResult();
    }

    public List<AccountSnapshot> findAllLatestBefore(Set<Account> accounts, LocalDateTime dateTime) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountSnapshot> query = criteriaBuilder.createQuery(AccountSnapshot.class);
        Root<AccountSnapshot> from = query.from(AccountSnapshot.class);

        Subquery<LocalDateTime> subQuery = query.subquery(LocalDateTime.class);
        Root<AccountSnapshot> fromSubQuery = subQuery.from(AccountSnapshot.class);
        subQuery.select(criteriaBuilder.greatest(fromSubQuery.<LocalDateTime>get("dateTime")));

        Predicate queryCriteria = criteriaBuilder.and(
                from.get("account").in(accounts),
                criteriaBuilder.lessThanOrEqualTo(from.get("dateTime"), Timestamp.valueOf(dateTime)));

        query.where(queryCriteria).orderBy(criteriaBuilder.desc(from.get("dateTime")));
        return entityManager.createQuery(query).getResultList();
    }

    public AccountSnapshot findEarliestAfter(Long accountId, LocalDateTime dateTime) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountSnapshot> query = criteriaBuilder.createQuery(AccountSnapshot.class);
        Root<AccountSnapshot> from = query.from(AccountSnapshot.class);

        Subquery<LocalDateTime> subQuery = query.subquery(LocalDateTime.class);
        Root<AccountSnapshot> fromSubQuery = subQuery.from(AccountSnapshot.class);
        subQuery.select(criteriaBuilder.least(fromSubQuery.<LocalDateTime>get("dateTime")));

        Predicate queryCriteria = criteriaBuilder.and(
                criteriaBuilder.equal(from.get("account"), accountId),
                criteriaBuilder.equal(from.get("dateTime"), subQuery));

        query.where(queryCriteria).orderBy(criteriaBuilder.desc(from.get("dateTime")));
        return entityManager.createQuery(query)
                .setFirstResult(0)
                .setMaxResults(1)
                .getSingleResult();
    }

    public List<AccountSnapshot> findAllEarliestAfter(Set<Account> accounts, LocalDateTime dateTime) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountSnapshot> query = criteriaBuilder.createQuery(AccountSnapshot.class);
        Root<AccountSnapshot> from = query.from(AccountSnapshot.class);

        Subquery<LocalDateTime> subQuery = query.subquery(LocalDateTime.class);
        Root<AccountSnapshot> fromSubQuery = subQuery.from(AccountSnapshot.class);
        subQuery.select(criteriaBuilder.least(fromSubQuery.<LocalDateTime>get("dateTime")));

        Predicate queryCriteria = criteriaBuilder.and(
                from.get("account").in(accounts),
                criteriaBuilder.equal(from.get("dateTime"), subQuery));

        query.where(queryCriteria).orderBy(criteriaBuilder.desc(from.get("dateTime")));
        return entityManager.createQuery(query).getResultList();
    }
}
