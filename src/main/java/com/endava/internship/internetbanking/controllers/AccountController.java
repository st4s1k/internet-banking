package com.endava.internship.internetbanking.controllers;

import com.endava.internship.internetbanking.dto.AccountDTO;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.User;
import com.endava.internship.internetbanking.sevices.AccountService;
import com.endava.internship.internetbanking.sevices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Value("${http.account_creation.fail}")
    private String accountCreationFail;

    @Value("${http.account_creation.fail.bad_user}")
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
    public ResponseEntity createAccount(@RequestBody AccountDTO dto) {

        Optional<User> accountOwner = userService.findById(dto.getUserId());
        Optional<Account> createdAccount = accountOwner.flatMap(user ->
                accountService.createAccount(user));

        ResponseEntity response;
        if (!accountOwner.isPresent()) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(accountCreationFailBadUser + " user id: " + dto.getUserId());
        } else if (!createdAccount.isPresent()) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(accountCreationFail);
        } else {
            response = ResponseEntity.ok(createdAccount.get().dto());
        }
        return response;
    }
}
