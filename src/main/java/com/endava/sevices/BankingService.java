package com.endava.sevices;

import com.endava.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BankingService {

    @Autowired
    private AccountService accountService;

    public void topUp(Account source,
                      Account destination,
                      BigDecimal funds) {
        transfer(source, destination, funds);
    }

    public void topUp(Long sourceId,
                      Long destinationId,
                      BigDecimal funds) {
        transfer(sourceId, destinationId, funds);
    }

    public void drawDown(Account source,
                         Account destination,
                         BigDecimal funds) {
        transfer(destination, source, funds);
    }

    public void drawDown(Long sourceId,
                         Long destinationId,
                         BigDecimal funds) {
        transfer(destinationId, sourceId, funds);
    }

    public void transfer(Long sourceId,
                         Long destinationId,
                         BigDecimal funds) {
        Optional<Account> source = accountService.findById(sourceId);
        Optional<Account> destination = accountService.findById(destinationId);
        if (!source.isPresent()) {
            throw new IllegalArgumentException("Account source with given ID does not exist."
                    + " [id: " + sourceId + "]");
        } else if (!destination.isPresent()) {
            throw new IllegalArgumentException("Account destination with given ID does not exist."
                    + " [id: " + destinationId + "]");
        } else {
            transfer(source.get(), destination.get(), funds);
        }
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
