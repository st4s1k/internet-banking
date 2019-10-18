package com.endava.internship.internetbanking.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BankingService {

    private final TransferService transferService;

    @Autowired
    public BankingService(TransferService transferService) {
        this.transferService = transferService;
    }

    public void topUp(Long currentAccountId,
                      Long targetAccountId,
                      BigDecimal funds) {

        transferService.transfer(currentAccountId, targetAccountId, funds);
    }

    public void drawDown(Long currentAccountId,
                         Long targetAccountId,
                         BigDecimal funds) {

        transferService.transfer(targetAccountId, currentAccountId, funds);
    }
}
