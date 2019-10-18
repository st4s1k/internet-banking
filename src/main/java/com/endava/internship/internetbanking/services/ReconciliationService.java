package com.endava.internship.internetbanking.services;

import com.endava.internship.internetbanking.repositories.TransferRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReconciliationService {

    private final TransferRepository transferRepository;

    @Autowired
    public ReconciliationService(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    @Scheduled(fixedDelay = 30_000)
    public void reconciliation() {
        log.info("Running reconciliation...");

        log.info("Reconciliation done.");
    }
}
