package com.endava.internship.internetbanking.services;

import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.AccountSnapshot;
import com.endava.internship.internetbanking.entities.Transfer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
public class ReconciliationService {

    private final AccountSnapshotService accountSnapshotService;
    private final AccountService accountService;
    private final TransferService transferService;
    private final Messages.Logging.Reconciliation msg;

    @Value("${internetbanking.env.reconciliation.rate}")
    long rate;

    @Autowired
    public ReconciliationService(AccountSnapshotService accountSnapshotService,
                                 AccountService accountService,
                                 TransferService transferService,
                                 Messages msg) {
        this.accountSnapshotService = accountSnapshotService;
        this.accountService = accountService;
        this.transferService = transferService;
        this.msg = msg.logging.reconciliation;
    }

    @Scheduled(fixedRateString = "${internetbanking.env.reconciliation.rate}")
    public void reconciliation() {

        log.info(msg.started);

        LocalDateTime lastReconciliationTime = now().minus(rate, MILLIS);
        List<Transfer> transfers = transferService.findAllAfter(lastReconciliationTime);

        if (!transfers.isEmpty()) {

            boolean balanceConsistencyIsOk;

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

            Map<Account, BigDecimal> expectedDifferential =
                    calculateBalanceDifferentialRegardingTransferHistory(lossMap, gainMap);

            Set<Account> accounts = expectedDifferential.keySet();

            List<AccountSnapshot> snapshots = accountSnapshotService.findAllEarliestAfter(accounts, lastReconciliationTime);

            Map<Account, BigDecimal> actualDifferential =
                    calculateBalanceDifferentialRegardingAccountHistory(snapshots);

            balanceConsistencyIsOk = actualDifferential.entrySet()
                    .stream()
                    .map(entry -> expectedDifferential.get(entry.getKey()).compareTo(entry.getValue()))
                    .reduce(0, Integer::sum)
                    .equals(0);

            if (balanceConsistencyIsOk) {
                log.info(msg.success);
            } else {
                log.warn(msg.fail);
            }
        }

        log.info(msg.ended);
    }

    private Map<Account, BigDecimal>
    calculateBalanceDifferentialRegardingAccountHistory(List<AccountSnapshot> snapshots) {
        return snapshots.stream().collect(toMap(
                AccountSnapshot::getAccount,
                snapshot -> snapshot.getFunds().subtract(snapshot.getAccount().getFunds())));
    }

    private Map<Account, BigDecimal>
    calculateBalanceDifferentialRegardingTransferHistory(Map<Account, BigDecimal> lossMap,
                                                         Map<Account, BigDecimal> gainMap) {
        return Stream.of(lossMap.keySet(), gainMap.keySet())
                .flatMap(Collection::parallelStream).collect(toMap(
                        account -> account,
                        account -> Optional.ofNullable(gainMap.get(account))
                                .flatMap(gain -> Optional.ofNullable(lossMap.get(account)).map(gain::subtract))
                                .orElse(ZERO)));
    }
}
