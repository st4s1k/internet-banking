package com.endava.internship.internetbanking.controllers;

import com.endava.internship.internetbanking.beans.ResponseBean;
import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.dto.AccountDTO;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.User;
import com.endava.internship.internetbanking.services.AccountService;
import com.endava.internship.internetbanking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${internetbanking.endpoints.accounts.url}")
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
    public ResponseEntity<ResponseBean> getAllAccounts() {
        List<AccountDTO> accounts = accountService.findAll().stream()
                .map(Account::dto)
                .collect(Collectors.toList());
        return accounts.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(ResponseBean.builder()
                .status(OK.value()).data(accounts).build());
    }

    @PostMapping
    public ResponseEntity<ResponseBean> createAccount(@RequestBody @Valid AccountDTO dto) {

        Optional<User> accountOwner = userService.findById(dto.getUserId());

        Optional<Account> createdAccount =
                accountOwner.flatMap(accountService::createAccount);

        ResponseEntity<ResponseBean> response;
        if (!accountOwner.isPresent()) {
            response = ResponseEntity.status(EXPECTATION_FAILED)
                    .body(ResponseBean.builder().status(EXPECTATION_FAILED.value())
                            .message(msg.creation.userNotFound).data(dto).build());
        } else if (!createdAccount.isPresent()) {
            response = ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(ResponseBean.builder().status(INTERNAL_SERVER_ERROR.value())
                            .message(msg.creation.fail).build());
        } else {
            response = ResponseEntity
                    .ok(ResponseBean.builder().status(OK.value())
                            .message(msg.creation.success).data(createdAccount.get().dto()).build());
        }
        return response;
    }
}
