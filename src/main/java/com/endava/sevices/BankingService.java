package com.endava.sevices;

import com.endava.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BankingService {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    public void topUp(Account source,
                      Account destination,
                      BigDecimal funds) {
        transfer(source, destination, funds);
    }

    public void drawDown(Account source,
                         Account destination,
                         BigDecimal funds) {
        transfer(destination, source, funds);
    }

    private void transfer(Account source,
                          Account destination,
                          BigDecimal funds) {
        Account newSource = new Account.Builder()
                .setId(source.getId())
                .setFunds(source.getFunds().subtract(funds))
                .setUser(source.getUser())
                .build();
        Account newDestination = new Account.Builder()
                .setId(destination.getId())
                .setFunds(destination.getFunds().add(funds))
                .setUser(destination.getUser())
                .build();
        if (!accountService.update(newSource).isPresent()
                || !accountService.update(newDestination).isPresent()) {
            accountService.update(source);
            accountService.update(destination);
        }
    }
}
