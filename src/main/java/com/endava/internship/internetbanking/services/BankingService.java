package com.endava.internship.internetbanking.services;

import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BankingService {

    private final AccountService accountService;

    private final Messages.Exceptions.Transfer msg;

    private static final BigDecimal _100_PERCENT_ = new BigDecimal(100);
    public static final BigDecimal ALLOWED_TRANSFER_QUOTA = new BigDecimal(40);
    public static final BigDecimal MINIMUM_TRANSFER_AMOUNT = new BigDecimal(10);

    @Autowired
    public BankingService(AccountService accountService,
                          Messages msg) {
        this.accountService = accountService;
        this.msg = msg.exceptions.transfer;
    }

    @Validated
    public boolean transferQuotaExceeded(@NotNull Account account,
                                         @NotNull BigDecimal funds) {
        return funds.divide(account.getFunds(), BigDecimal.ROUND_FLOOR)
                .multiply(_100_PERCENT_)
                .compareTo(ALLOWED_TRANSFER_QUOTA) > 0;
    }

    public void topUp(Long currentAccountId,
                      Long targetAccountId,
                      BigDecimal funds)
            throws
            InvalidSourceAccountException,
            InvalidDestinationAccountException,
            TransferQuotaExceededException,
            InsufficientTransferFundsException,
            InsufficientSourceFundsException {

        transfer(currentAccountId, targetAccountId, funds);
    }

    public void drawDown(Long currentAccountId,
                         Long targetAccountId,
                         BigDecimal funds)
            throws
            InvalidSourceAccountException,
            InvalidDestinationAccountException,
            TransferQuotaExceededException,
            InsufficientTransferFundsException,
            InsufficientSourceFundsException {

        transfer(targetAccountId, currentAccountId, funds);
    }

    private void transfer(Long sourceId,
                          Long destinationId,
                          BigDecimal funds)
            throws
            InvalidSourceAccountException,
            InvalidDestinationAccountException,
            TransferQuotaExceededException,
            InsufficientTransferFundsException,
            InsufficientSourceFundsException {

        Optional<Account> source = accountService.findById(sourceId);
        Optional<Account> destination = accountService.findById(destinationId);
        if (!source.isPresent()) {
            throw new InvalidSourceAccountException(msg.badDestinationId
                    + " [id: " + sourceId + "]");
        }
        if (!destination.isPresent()) {
            throw new InvalidDestinationAccountException(msg.badDestinationId
                    + " [id: " + destinationId + "]");
        }

        transfer(source.get(), destination.get(), funds);

    }

    @Validated
    @Transactional
    public void transfer(@NotNull Account source,
                         @NotNull Account destination,
                         @NotNull BigDecimal funds)
            throws
            TransferQuotaExceededException,
            InsufficientTransferFundsException,
            InsufficientSourceFundsException {

        if (funds.compareTo(MINIMUM_TRANSFER_AMOUNT) < 0) {
            throw new InsufficientTransferFundsException();
        }

        if (source.getFunds().compareTo(funds) < 0) {
            throw new InsufficientSourceFundsException();
        }

        if (transferQuotaExceeded(source, funds)) {
            throw new TransferQuotaExceededException();
        }

        source.setFunds(source.getFunds().subtract(funds));
        destination.setFunds(destination.getFunds().add(funds));

        accountService.update(source);
        accountService.update(destination);
    }
}
