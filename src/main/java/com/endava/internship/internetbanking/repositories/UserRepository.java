package com.endava.internship.internetbanking.repositories;

import com.endava.internship.internetbanking.entities.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

@Repository
@Transactional(REQUIRES_NEW)
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<User> save(User user) {
        entityManager.persist(user);
        entityManager.flush();
        return Optional.of(user).filter(u -> u.getId() != null);
    }

    public Optional<User> remove(User user) {
        Optional<User> userToBeRemoved = findById(user.getId());
        userToBeRemoved.ifPresent(_user ->
                entityManager.remove(entityManager.contains(_user) ? _user : entityManager.merge(_user)));
        return userToBeRemoved;
    }

    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(entityManager.find(User.class, userId));
    }

    public Optional<User> findByName(String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> from = query.from(User.class);
        Predicate userHasName = criteriaBuilder.equal(from.get("name"), name);
        query.where(userHasName);
        return entityManager.createQuery(query)
                .getResultStream()
                .findAny();
    }

    public List<User> findAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> from = query.from(User.class);
        query.select(from);
        return entityManager.createQuery(query).getResultList();
    }

    public Optional<User> update(User user) {
        return Optional.ofNullable(entityManager.merge(user));
    }
}
