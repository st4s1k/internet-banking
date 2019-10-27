package com.endava.internship.internetbanking.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Service
public class BankingService {

    private final TransferService transferService;
    private final long _processing_time_ = SECONDS.toMillis(10);

    @Autowired
    public BankingService(TransferService transferService) {
        this.transferService = transferService;
    }

    public void refill(Long accountId, BigDecimal funds) {
        try {
            log.info("BankingService#refill Executing operation...");
            Thread.sleep(_processing_time_);
            log.info("BankingService#refill Done.");
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    public void topUp(Long currentAccountId,
                      Long targetAccountId,
                      BigDecimal funds) {
        try {
            log.info("BankingService#topUp Executing operation...");
            Thread.sleep(_processing_time_);
            transferService.transfer(currentAccountId, targetAccountId, funds);
            log.info("BankingService#topUp Done.");
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    public void drawDown(Long currentAccountId,
                         Long targetAccountId,
                         BigDecimal funds) {
        try {
            log.info("BankingService#drawDown Executing operation...");
            Thread.sleep(_processing_time_);
            transferService.transfer(targetAccountId, currentAccountId, funds);
            log.info("BankingService#drawDown Done.");
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
