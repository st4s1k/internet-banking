package com.endava.internship.internetbanking.repositories;

import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class AccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Account> save(Account account) {
        entityManager.persist(account);
        entityManager.flush();
        return Optional.of(account).filter(a -> a.getId() != null);
    }

    public Optional<Account> remove(Account account) {
        entityManager.remove(account);
        return findById(account.getId());
    }

    public Optional<Account> findById(Long accountId) {
        return Optional.of(entityManager.find(Account.class, accountId));
    }

    public List<Account> findByUser(User user) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> query = criteriaBuilder.createQuery(Account.class);
        Root<Account> from = query.from(Account.class);
        Predicate userId = criteriaBuilder.equal(from.get("user_id"), user.getId());
        query.where(userId);
        return entityManager.createQuery(query).getResultList();
    }

    public List<Account> findAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> query = criteriaBuilder.createQuery(Account.class);
        Root<Account> from = query.from(Account.class);
        query.select(from);
        return entityManager.createQuery(query).getResultList();
    }

    public Optional<Account> update(Account account) {
        entityManager.merge(account);
        return findById(account.getId());
    }
}
