package com.endava.controllers;

import com.endava.dto.AccountDTO;
import com.endava.entities.Account;
import com.endava.sevices.AccountService;
import com.endava.sevices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity getAllAccounts() {
        List<Account> accounts = accountService.findAll();
        return accounts.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(accounts);
    }

    @PutMapping
    public ResponseEntity createAccount(@RequestBody AccountDTO accountDTO) {
        return userService.findById(accountDTO.getUser().getId())
                .map(user -> accountService.createAccount(user)
                        .map(account -> ResponseEntity.ok("Account for user " +
                                account.getUser().getName() + " successfully created"))
                        .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to create account for user " + user.getName())))
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("No user found with id: " + accountDTO.getUser().getId()));
    }
}
