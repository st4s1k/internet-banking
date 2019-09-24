package com.endava.controllers;

import com.endava.entities.User;
import com.endava.sevices.AccountService;
import com.endava.sevices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity getAllAccounts() {
        return new ResponseEntity<>(accountService.findAll(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity createAccount(@RequestBody Long userId) {
        Optional<User> user = userService.findById(userId);
        return user.<ResponseEntity>map(value -> accountService.createAccount(value)
                ? new ResponseEntity<>("Account for user " + value.getName() + " successfully created", HttpStatus.OK)
                : new ResponseEntity<>("Failed to create account for user " + value.getName(),
                HttpStatus.EXPECTATION_FAILED))
                .orElseGet(() -> new ResponseEntity<>("No user found with id: " + userId,
                        HttpStatus.EXPECTATION_FAILED));
    }
}
