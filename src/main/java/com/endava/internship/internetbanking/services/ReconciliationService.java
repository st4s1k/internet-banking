package com.endava.internship.internetbanking.services;

import com.endava.internship.internetbanking.config.InternetBankingEnv;
import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.AccountSnapshot;
import com.endava.internship.internetbanking.entities.Transfer;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
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
    private final TransferService transferService;
    private final Messages.Logging.Reconciliation msg;
    private final InternetBankingEnv.Reconciliation env;
    private final Gson gson;

    @Autowired
    public ReconciliationService(AccountSnapshotService accountSnapshotService,
                                 TransferService transferService,
                                 Messages msg,
                                 InternetBankingEnv env,
                                 @Qualifier("prettyPrintJSON") Gson gson) {
        this.accountSnapshotService = accountSnapshotService;
        this.transferService = transferService;
        this.msg = msg.logging.reconciliation;
        this.env = env.reconciliation;
        this.gson = gson;
    }

    @Scheduled(fixedRateString = "${internetbanking.env.reconciliation.rate}")
    public void reconciliation() {

        log.info(msg.started);

        LocalDateTime lastReconciliationTime = now().minus(env.rate, MILLIS);

        List<Transfer> transfers = transferService.findAllAfter(lastReconciliationTime);

        LocalDateTime firstTransferAfterLastReconciliation = transfers.stream()
                .min(Comparator.comparing(Transfer::getDateTime))
                .map(Transfer::getDateTime)
                .orElse(lastReconciliationTime);

        if (!transfers.isEmpty()) {

            boolean balanceConsistencyIsOk;

            Map<Account, List<Transfer>> sourceGroups = transfers.parallelStream()
                    .collect(groupingBy(Transfer::getSourceAccount));

            Map<Account, List<Transfer>> destinationGroups = transfers.parallelStream()
                    .collect(groupingBy(Transfer::getDestinationAccount));

            Set<Account> accounts = transfers.stream()
                    .flatMap(t -> Stream.of(t.getSourceAccount(), t.getDestinationAccount()))
                    .collect(Collectors.toSet());

            List<AccountSnapshot> snapshots =
                    accountSnapshotService.findAllLatestBefore(accounts, firstTransferAfterLastReconciliation);

            balanceConsistencyIsOk = snapshots.size() >= transfers.size();

            if (balanceConsistencyIsOk) {

                Map<Account, BigDecimal> lossMap = reduceTransfers(sourceGroups);

                Map<Account, BigDecimal> gainMap = reduceTransfers(destinationGroups);

                Map<Account, BigDecimal> expectedDifferential =
                        calculateBalanceDifferentialRegardingTransferHistory(accounts, lossMap, gainMap);

                Map<Account, BigDecimal> actualDifferential =
                        calculateBalanceDifferentialRegardingAccountHistory(accounts, snapshots);

                balanceConsistencyIsOk = actualDifferential.entrySet().stream()
                        .map(entry -> expectedDifferential.get(entry.getKey())
                                .subtract(entry.getValue()).equals(ZERO))
                        .reduce(true, Boolean::logicalAnd);
            }

            if (balanceConsistencyIsOk) {
                log.info(msg.success);
            } else {
                log.warn(msg.fail);
            }
        }

        log.info(msg.ended);
    }

    private Map<Account, BigDecimal> reduceTransfers(Map<Account, List<Transfer>> groups) {
        return groups.entrySet().parallelStream().collect(toMap(
                Map.Entry::getKey,
                group -> group.getValue().stream()
                        .map(Transfer::getFunds)
                        .reduce(ZERO, BigDecimal::add)));
    }

    private Map<Account, BigDecimal>
    calculateBalanceDifferentialRegardingAccountHistory(Set<Account> accounts,
                                                        List<AccountSnapshot> snapshots) {
        return accounts.parallelStream().collect(toMap(
                account -> account,
                account -> snapshots.stream()
                        .filter(s -> s.getAccount().equals(account))
                        .findAny()
                        .map(AccountSnapshot::getFunds)
                        .orElse(ZERO)
                        .subtract(account.getFunds())));
    }

    private Map<Account, BigDecimal>
    calculateBalanceDifferentialRegardingTransferHistory(Set<Account> accounts,
                                                         Map<Account, BigDecimal> lossMap,
                                                         Map<Account, BigDecimal> gainMap) {
        return accounts.parallelStream().collect(toMap(
                account -> account,
                account -> Optional.ofNullable(gainMap.get(account)).orElse(ZERO)
                        .subtract(Optional.ofNullable(lossMap.get(account)).orElse(ZERO))));
    }
}
