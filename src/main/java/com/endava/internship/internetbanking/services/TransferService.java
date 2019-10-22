package com.endava.internship.internetbanking.services;

import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.Transfer;
import com.endava.internship.internetbanking.exceptions.*;
import com.endava.internship.internetbanking.repositories.TransferRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

import static java.math.RoundingMode.FLOOR;

@Slf4j

@Service
@SuppressWarnings("WeakerAccess")
public class TransferService {

    private final TransferRepository transferRepository;

    private final AccountService accountService;
    private final Messages.Exceptions.Transfer msg;

    public static final BigDecimal _100_PERCENT_ = new BigDecimal(100);
    public static final BigDecimal ALLOWED_TRANSFER_QUOTA = new BigDecimal(40);
    public static final BigDecimal MINIMUM_TRANSFER_AMOUNT = new BigDecimal(10);

    @Autowired
    public TransferService(TransferRepository transferRepository,
                           AccountService accountService,
                           Messages msg) {
        this.transferRepository = transferRepository;
        this.accountService = accountService;
        this.msg = msg.exceptions.transfer;
    }

    public boolean transferQuotaExceeded(Account account,
                                         BigDecimal funds) {
        return account != null
                && account.getFunds() != null
                && funds != null
                && account.getFunds().compareTo(funds) > 0
                && funds
                .divide(account.getFunds(), FLOOR)
                .multiply(_100_PERCENT_)
                .compareTo(ALLOWED_TRANSFER_QUOTA) > 0;
    }

    public void transfer(Long sourceId,
                         Long destinationId,
                         BigDecimal funds) {
        try {
            Optional<Account> source = accountService.findById(sourceId);
            Optional<Account> destination = accountService.findById(destinationId);
            if (!source.isPresent()) {
                throw new InvalidSourceAccountException(msg.badSourceId
                        + " [id: " + sourceId + "]");
            }
            if (!destination.isPresent()) {
                throw new InvalidDestinationAccountException(msg.badDestinationId
                        + " [id: " + destinationId + "]");
            }
            transfer(source.get(), destination.get(), funds);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void transfer(Account source,
                         Account destination,
                         BigDecimal funds) {

        preTransferChecks(source, funds);

        source.setFunds(source.getFunds().subtract(funds));
        destination.setFunds(destination.getFunds().add(funds));
        Optional<Account> updSource = accountService.update(source);
        Optional<Account> updDestination = accountService.update(destination);

        postTransferChecks(source, destination, funds, updSource, updDestination);

        logTransfer(source, destination, funds);
    }

    private void preTransferChecks(Account source, BigDecimal funds) {

        if (funds.compareTo(MINIMUM_TRANSFER_AMOUNT) < 0) {
            RuntimeException rte = new InsufficientTransferFundsException();
            log.error(msg.invalidTransferAmount, rte);
            throw rte;
        }

        if (source.getFunds().compareTo(funds) < 0) {
            RuntimeException rte = new InsufficientSourceFundsException();
            log.error(msg.insufficientFunds, rte);
            throw rte;
        }

        if (transferQuotaExceeded(source, funds)) {
            RuntimeException rte = new TransferQuotaExceededException();
            log.error(msg.invalidTransferAmount, rte);
            throw rte;
        }
    }

    private void postTransferChecks(Account source,
                                    Account destination,
                                    BigDecimal funds,
                                    Optional<Account> updSource,
                                    Optional<Account> updDestination) {
        if (!updSource.map(_updSrc -> _updSrc.equals(source)).orElse(false)) {
            RuntimeException rte = new TransferFailedException();
            log.error(msg.fail, rte);
            throw rte;
        }

        if (!updDestination.map(_updDst -> _updDst.equals(destination)).orElse(false)) {
            RuntimeException rte = new TransferFailedException();
            log.error(msg.fail, rte);
            throw rte;
        }
    }

    private void logTransfer(Account source, Account destination, BigDecimal funds) {
        transferRepository.save(
                Transfer.builder()
                        .funds(funds)
                        .sourceAccount(source)
                        .destinationAccount(destination)
                        .build())
                .orElseThrow(TransferLoggingException::new);
    }
}
