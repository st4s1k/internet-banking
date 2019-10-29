package com.endava.internship.internetbanking.repositories;

import com.endava.internship.internetbanking.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@Transactional(propagation = MANDATORY)
public class UserRepository {

    private final EntityManager entityManager;

    @Autowired
    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<User> save(User user) {
        entityManager.persist(user);
        return find(user);
    }

    public Optional<User> update(User user) {
        entityManager.merge(user);
        return find(user);
    }

    public Optional<User> remove(User user) {
        Optional<User> userToBeRemoved = findById(user.getId());
        userToBeRemoved.ifPresent(_user ->
                entityManager.remove(entityManager.contains(_user) ? _user : entityManager.merge(_user)));
        return userToBeRemoved;
    }

    public List<User> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> userFields = query.from(User.class);

        query.select(userFields);

        return entityManager.createQuery(query).getResultList();
    }

    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(entityManager.find(User.class, userId));
    }

    public Optional<User> find(User user) {
        return Optional.ofNullable(user)
                .flatMap(u -> Optional.ofNullable(u.getId()))
                .flatMap(this::findById);
    }

    public Optional<User> findByName(String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> userFields = query.from(User.class);

        query.select(userFields)
                .where(cb.equal(userFields.get("name"), name));

        return entityManager.createQuery(query)
                .getResultStream()
                .findAny();
    }
}
