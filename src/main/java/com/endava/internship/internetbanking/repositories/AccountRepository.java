package com.endava.internship.internetbanking.repositories;

import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class AccountRepository {

    private final EntityManager entityManager;

    @Autowired
    public AccountRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Account> save(Account account) {
        entityManager.persist(account);
        return find(account);
    }

    public Optional<Account> update(Account account) {
        entityManager.merge(account);
        return find(account);
    }

    private Optional<Account> find(Account account) {
        return Optional.ofNullable(account)
                .flatMap(a -> Optional.ofNullable(a.getId()))
                .flatMap(this::findById);
    }

    public Optional<Account> remove(Account account) {
        Optional<Account> accountToBeRemoved = findById(account.getId());
        accountToBeRemoved.ifPresent(_account -> entityManager.remove(
                entityManager.contains(_account) ? _account : entityManager.merge(_account)));
        return accountToBeRemoved;
    }

    public List<Account> findAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> query = criteriaBuilder.createQuery(Account.class);
        Root<Account> from = query.from(Account.class);
        query.select(from);
        return entityManager.createQuery(query).getResultList();
    }

    public Optional<Account> findById(Long accountId) {
        return Optional.ofNullable(entityManager.find(Account.class, accountId));
    }

    public List<Account> findByUserId(Long userId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> query = criteriaBuilder.createQuery(Account.class);
        Root<Account> from = query.from(Account.class);
        Predicate userIdCriteria = criteriaBuilder.equal(from.get("user"), userId);
        query.where(userIdCriteria);
        return entityManager.createQuery(query).getResultList();
    }

    public List<Account> findByUser(User user) {
        return Optional.ofNullable(user)
                .flatMap(u -> Optional.ofNullable(u.getId()))
                .map(this::findByUserId)
                .orElse(emptyList());
    }
}
