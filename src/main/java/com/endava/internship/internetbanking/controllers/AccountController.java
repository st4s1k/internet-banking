package com.endava.internship.internetbanking.controllers;

import com.endava.internship.internetbanking.beans.ResponseBean;
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

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${endpoints.accounts.url}")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;
    private final Messages.Http.Account msg;

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
                : ResponseEntity.ok(ResponseBean.from(OK, accounts));
    }

    @PostMapping
    public ResponseEntity createAccount(@RequestBody @Valid AccountDTO dto) {

        Optional<User> accountOwner = userService.findById(dto.getUserId());

        Optional<com.endava.internship.internetbanking.entities.Account> createdAccount =
                accountOwner.flatMap(accountService::createAccount);

        ResponseEntity response;
        if (!accountOwner.isPresent()) {
            response = ResponseEntity.status(EXPECTATION_FAILED)
                    .body(ResponseBean.from(EXPECTATION_FAILED, msg.creation.userNotFound, dto));
        } else if (!createdAccount.isPresent()) {
            response = ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(ResponseBean.from(INTERNAL_SERVER_ERROR, msg.creation.fail));
        } else {
            response = ResponseEntity
                    .ok(ResponseBean.from(OK, msg.creation.success, createdAccount.get().dto()));
        }
        return response;
    }
}
