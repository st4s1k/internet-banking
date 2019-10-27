package com.endava.internship.internetbanking.controllers;

import com.endava.internship.internetbanking.beans.ResponseBean;
import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.dto.AccountDTO;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.services.AccountService;
import com.endava.internship.internetbanking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${internetbanking.endpoints.accounts.url}")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    private final Function<List<AccountDTO>, ResponseEntity<ResponseBean>> noContentResponse;
    private final Function<List<AccountDTO>, Supplier<ResponseEntity<ResponseBean>>> allAccountsResponse;
    private final Function<AccountDTO, ResponseEntity<ResponseBean>> userNotFoundResponse;
    private final Supplier<ResponseEntity<ResponseBean>> internalServerErrorResponse;
    private final Function<Account, ResponseEntity<ResponseBean>> accountCreationSuccessResponse;

    @Autowired
    public AccountController(AccountService accountService,
                             UserService userService,
                             Messages msg) {
        this.accountService = accountService;
        this.userService = userService;

        this.noContentResponse = a ->
                ResponseEntity.status(NO_CONTENT).body(ResponseBean.builder()
                        .status(NO_CONTENT.value())
                        .message(msg.http.account.noContent)
                        .build());

        this.allAccountsResponse = accounts -> () ->
                ResponseEntity.ok(ResponseBean.builder()
                        .status(OK.value())
                        .data(accounts)
                        .build());

        this.userNotFoundResponse = dto ->
                ResponseEntity.status(EXPECTATION_FAILED).body(ResponseBean.builder()
                        .status(EXPECTATION_FAILED.value())
                        .message(msg.http.account.creation.userNotFound)
                        .data(dto)
                        .build());

        this.internalServerErrorResponse = () ->
                ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ResponseBean.builder()
                        .status(INTERNAL_SERVER_ERROR.value())
                        .message(msg.http.account.creation.fail)
                        .build());

        this.accountCreationSuccessResponse = account ->
                ResponseEntity.ok(ResponseBean.builder()
                        .status(OK.value())
                        .message(msg.http.account.creation.success)
                        .data(account.dto())
                        .build());
    }

    @GetMapping
    public ResponseEntity<ResponseBean> getAllAccounts() {
        List<AccountDTO> accounts = accountService.findAll().stream()
                .map(Account::dto)
                .collect(Collectors.toList());
        return Optional.of(accounts)
                .filter(List::isEmpty)
                .map(noContentResponse)
                .orElseGet(allAccountsResponse.apply(accounts));
    }

    @PostMapping
    public ResponseEntity<ResponseBean> createAccount(@RequestBody @Valid AccountDTO dto) {
        return userService.findById(dto.getUserId())
                .map(accountService::createAccount)
                .map(createdAccount -> createdAccount
                        .map(accountCreationSuccessResponse)
                        .orElseGet(internalServerErrorResponse))
                .orElse(userNotFoundResponse.apply(dto));
    }
}
