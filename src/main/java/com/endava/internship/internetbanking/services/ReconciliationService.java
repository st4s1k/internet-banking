package com.endava.internship.internetbanking.services;

import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.Transfer;
import com.endava.internship.internetbanking.repositories.TransferRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
public class ReconciliationService {

    private final TransferRepository transferRepository;
    private Messages.Logging.Reconciliation msg;

    @Autowired
    public ReconciliationService(TransferRepository transferRepository,
                                 Messages msg) {
        this.transferRepository = transferRepository;
        this.msg = msg.logging.reconciliation;
    }

    @Scheduled(fixedDelay = 30_000)
    public void reconciliation() {

        log.info(msg.started);

        List<Transfer> transfers = transferRepository.findAll();

        Map<Account, List<Transfer>> sourceGroups = transfers.parallelStream()
                .collect(groupingBy(Transfer::getSourceAccount));

        Map<Account, List<Transfer>> destinationGroups = transfers.parallelStream()
                .collect(groupingBy(Transfer::getDestinationAccount));

        Map<Account, BigDecimal> lossMap = sourceGroups.entrySet().parallelStream()
                .collect(toMap(Map.Entry::getKey, group ->
                        group.getValue().stream().map(Transfer::getFunds).reduce(ZERO, BigDecimal::add)));

        Map<Account, BigDecimal> gainMap = destinationGroups.entrySet().parallelStream()
                .collect(toMap(Map.Entry::getKey, group ->
                        group.getValue().stream().map(Transfer::getFunds).reduce(ZERO, BigDecimal::add)));

        Map<Account, BigDecimal> differential = calculateBalanceDifferential(lossMap, gainMap);

        BigDecimal differentialSum = differential.values().stream().reduce(ZERO, BigDecimal::add);

        if (differentialSum.equals(ZERO)) {
            log.info(msg.success);
        } else {
            log.warn(msg.fail);
        }

        log.info(msg.ended);
    }

    private Map<Account, BigDecimal> calculateBalanceDifferential(Map<Account, BigDecimal> lossMap,
                                                                  Map<Account, BigDecimal> gainMap) {
        return Stream.of(lossMap.keySet(), gainMap.keySet()).flatMap(Collection::parallelStream).collect(toMap(
                account -> account,
                account -> Optional.ofNullable(gainMap.get(account))
                        .flatMap(gain -> Optional.ofNullable(lossMap.get(account)).map(gain::subtract))
                        .orElse(ZERO)));
    }
}
