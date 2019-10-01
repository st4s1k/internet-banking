package com.endava.repositories;

import com.endava.entities.User;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    @Autowired
    private Session session;

    public User save(User user) {
        return (User) session.save(user);
    }

    public User remove(User user) {
        session.
    }
}
