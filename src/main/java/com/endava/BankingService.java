package com.endava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BankingService {

    @Autowired
    private UserService userService;

    public void topUp(Long id, Double funds) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            // top up for user
        }
    }
}
