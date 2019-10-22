package com.endava.internship.internetbanking.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
public class BankingService {

    private final TransferService transferService;

    @Autowired
    public BankingService(TransferService transferService) {
        this.transferService = transferService;
    }

    public void refill(Long accountId, BigDecimal funds) {

    }

    public void topUp(Long currentAccountId,
                      Long targetAccountId,
                      BigDecimal funds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(30));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        transferService.transfer(currentAccountId, targetAccountId, funds);
    }

    public void drawDown(Long currentAccountId,
                         Long targetAccountId,
                         BigDecimal funds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(30));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        transferService.transfer(targetAccountId, currentAccountId, funds);
    }
}
