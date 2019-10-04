package com.endava.internship.internetbanking.controllers;

import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.dto.AccountDTO;
import com.endava.internship.internetbanking.entities.User;
import com.endava.internship.internetbanking.services.AccountService;
import com.endava.internship.internetbanking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.EXPECTATION_FAILED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private AccountService accountService;
    private UserService userService;
    private Messages.Http.Account msg;

    @Autowired
    public AccountController(AccountService accountService,
                             UserService userService,
                             Messages msg) {
        this.accountService = accountService;
        this.userService = userService;
        this.msg = msg.http.account;
    }

    @GetMapping
    public ResponseEntity getAllAccounts() {
        List<com.endava.internship.internetbanking.entities.Account> accounts = accountService.findAll();
        return accounts.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(accounts);
    }

    @PutMapping
    // TODO: add validation on request
    public ResponseEntity createAccount(@RequestBody @Valid AccountDTO dto) {

        Optional<User> accountOwner = userService.findById(dto.getUserId());

        Optional<com.endava.internship.internetbanking.entities.Account> createdAccount = accountOwner.flatMap(user ->
                accountService.createAccount(user));

        ResponseEntity response;
        if (!accountOwner.isPresent()) {
            // TODO: choose a better code
            response = ResponseEntity.status(EXPECTATION_FAILED)
                    .body(msg.creation.userNotFound + " user id: " + dto.getUserId());
        } else if (!createdAccount.isPresent()) {
            response = ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(msg.creation.fail);
        } else {
            response = ResponseEntity.ok(createdAccount.get().dto()); // msg.accountCreation.success
        }
        return response;
    }
}
