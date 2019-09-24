package com.endava.sevices;

import com.endava.entities.Account;
import com.endava.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.UnaryOperator;

@Service
public class BankingService {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    public boolean topUp(Long userId, BigDecimal funds) {
        return changeFunds(userId, funds::add);
    }

    private boolean changeFunds(Long userId, UnaryOperator<BigDecimal> operator) {
        Optional<User> user = userService.findById(userId);
        Optional<Account> account = user.flatMap(_user -> accountService.findByUser(_user));
        account.ifPresent(acc -> acc.setFunds(operator.apply(account.get().getFunds())));
        return account.map(accountService::update).orElse(false);
    }
}
