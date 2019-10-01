package com.endava.repositories;

import com.endava.entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AccountRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public Optional<User> save(User user) {
        Session session = sessionFactory.getCurrentSession();
        User savedUser = (User) session.save(user);
        return Optional.ofNullable(savedUser);
    }
}
