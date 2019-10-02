package com.endava.internship.internetbanking.repositories;

import com.endava.internship.internetbanking.entities.User;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<User> save(User user) {
        Session session = entityManager.unwrap(Session.class);
        Long savedUserId = (Long) session.save(user);
        return findById(savedUserId);
    }

    public Optional<User> remove(User user) {
        Session session = entityManager.unwrap(Session.class);
        session.remove(user);
        return findById(user.getId());
    }

    public Optional<User> findById(Long userId) {
        Session session = entityManager.unwrap(Session.class);
        return Optional.of(session.find(User.class, userId));
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
        Session session = entityManager.unwrap(Session.class);
        session.update(user);
        return findById(user);
    }
}
