package com.endava.internship.internetbanking.controllers;

import com.endava.internship.internetbanking.dto.AccountDTO;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.sevices.AccountService;
import com.endava.internship.internetbanking.sevices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Value("account.creation.success")
    private String accountCreationSuccess;

    @Value("account.creation.fail")
    private String accountCreationFail;

    @Value("account.creation.fail.bad.user")
    private String accountCreationFailBadUser;

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
        return userService.findById(accountDTO.getUserId())
                .map(user -> accountService.createAccount(user)
                        .map(account -> ResponseEntity.ok(accountCreationSuccess +
                                " for user: " + account.getUser().getName()))
                        .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(accountCreationFail + " for user: " + user.getName())))
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(accountCreationFailBadUser + " id: " + accountDTO.getUserId()));
    }
}
