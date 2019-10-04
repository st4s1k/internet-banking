package com.endava.internship.internetbanking.sevices;

import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BankingService {

    @Value("exception.transfer.bad.source.id")
    private String badSourceIdMessage;

    @Value("exception.transfer.bad.destination.id")
    private String badDestinationIdMessage;

    @Autowired
    private AccountService accountService;

    public static double ALLOWED_TRANSFER_QUOTE = 40d / 100d;
    public static BigDecimal MINIMAL_TRANSFER_AMOUNT = BigDecimal.TEN;

    public void topUp(Long currentAccountId,
                      Long targetAccountId,
                      BigDecimal funds)
            throws
            InvalidSourceAccountException,
            InvalidDestinationAccountException,
            TransferQuoteExceededException,
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
            TransferQuoteExceededException,
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
            TransferQuoteExceededException,
            InsufficientTransferFundsException,
            InsufficientSourceFundsException {

        Optional<Account> source = accountService.findById(sourceId);
        Optional<Account> destination = accountService.findById(destinationId);
        if (!source.isPresent()) {
            throw new InvalidSourceAccountException(badSourceIdMessage
                    + " [id: " + sourceId + "]");
        } else if (!destination.isPresent()) {
            throw new InvalidDestinationAccountException(badDestinationIdMessage
                    + " [id: " + destinationId + "]");
        } else {
            transfer(source.get(), destination.get(), funds);
        }
    }

    @Transactional
    private void transfer(Account source,
                          Account destination,
                          BigDecimal funds)
            throws
            TransferQuoteExceededException,
            InsufficientTransferFundsException,
            InsufficientSourceFundsException {

        if (funds.compareTo(MINIMAL_TRANSFER_AMOUNT) < 0) {
            throw new InsufficientTransferFundsException();
        }

        if (source.getFunds().compareTo(funds) < 0) {
            throw new InsufficientSourceFundsException();
        }

        if (funds.doubleValue() / source.getFunds().doubleValue() > ALLOWED_TRANSFER_QUOTE) {
            throw new TransferQuoteExceededException();
        }

        source.setFunds(source.getFunds().subtract(funds));
        destination.setFunds(destination.getFunds().add(funds));

        accountService.update(source);
        accountService.update(destination);
    }
}
