package com.endava.sevices;

import com.endava.entities.Account;
import com.endava.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BankingService {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    public boolean topUp(Long id, Double funds) {
        Optional<User> user = userService.findById(id);
        Optional<Account> account = user.flatMap(_user -> accountService.findByUser(_user));
        // TODO: Implement topUp()
        return account.map(accountService::update).orElse(false);
    }
}
