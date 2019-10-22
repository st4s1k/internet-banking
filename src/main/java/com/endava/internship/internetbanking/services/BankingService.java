package com.endava.internship.internetbanking.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Service
public class BankingService {

    private final TransferService transferService;
    private final TaskExecutor taskExecutor;
    private final long _30_SECONDS_ = SECONDS.toMillis(30);

    @Autowired
    public BankingService(TransferService transferService,
                          TaskExecutor taskExecutor) {
        this.transferService = transferService;
        this.taskExecutor = taskExecutor;
    }

    public void refill(Long accountId, BigDecimal funds) {
        taskExecutor.execute(() -> {
            log.info("Executing BankingService#refill(Long, BigDecimal) operation...");
            try {
                Thread.sleep(_30_SECONDS_);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        });
    }

    public void topUp(Long currentAccountId,
                      Long targetAccountId,
                      BigDecimal funds) {
        taskExecutor.execute(() -> {
            log.info("Executing BankingService#topUp(Long, Long, BigDecimal) operation...");
            try {
                Thread.sleep(_30_SECONDS_);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
            transferService.transfer(currentAccountId, targetAccountId, funds);
        });
    }

    public void drawDown(Long currentAccountId,
                         Long targetAccountId,
                         BigDecimal funds) {
        taskExecutor.execute(() -> {
            log.info("Executing BankingService#drawDown(Long, Long, BigDecimal) operation...");
            try {
                Thread.sleep(_30_SECONDS_);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
            transferService.transfer(targetAccountId, currentAccountId, funds);
        });
    }
}
