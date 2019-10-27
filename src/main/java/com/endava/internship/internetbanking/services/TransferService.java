package com.endava.internship.internetbanking.services;

import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.dto.ITransferDTO;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.Transfer;
import com.endava.internship.internetbanking.exceptions.*;
import com.endava.internship.internetbanking.repositories.TransferRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static java.math.RoundingMode.FLOOR;
import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

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

    @Transactional(REQUIRES_NEW)
    public List<Transfer> findAll() {
        return transferRepository.findAll();
    }

    @Transactional(REQUIRES_NEW)
    public List<Transfer> findAllBefore(LocalDateTime dateTime) {
        return transferRepository.findAllBefore(dateTime);
    }

    @Transactional(REQUIRES_NEW)
    public List<Transfer> findAllAfter(LocalDateTime dateTime) {
        return transferRepository.findAllAfter(dateTime);
    }

    @Transactional(REQUIRES_NEW)
    public List<Transfer> findAllBefore(Account account, LocalDateTime dateTime) {
        return transferRepository.findAllBefore(account, dateTime);
    }

    @Transactional(REQUIRES_NEW)
    public List<Transfer> findAllAfter(Account account, LocalDateTime dateTime) {
        return transferRepository.findAllAfter(account, dateTime);
    }

    @Transactional(REQUIRES_NEW)
    public List<Transfer> findAllBefore(Set<Account> accounts, LocalDateTime dateTime) {
        return transferRepository.findAllBefore(accounts, dateTime);
    }

    @Transactional(REQUIRES_NEW)
    public List<Transfer> findAllAfter(Set<Account> accounts, LocalDateTime dateTime) {
        return transferRepository.findAllAfter(accounts, dateTime);
    }

    @Transactional(REQUIRES_NEW)
    public Optional<Transfer> findById(Long id) {
        return transferRepository.findById(id);
    }

    @Transactional(REQUIRES_NEW)
    public List<Transfer> findByAccountId(Long accountId) {
        return transferRepository.findByAccountId(accountId);
    }

    @Transactional(REQUIRES_NEW)
    public List<Transfer> findByAccount(Account account) {
        return transferRepository.findByAccount(account);
    }

    @Transactional(REQUIRES_NEW)
    public void transfer(@Nullable Long sourceId,
                         @Nullable Long destinationId,
                         @Nullable BigDecimal funds) {
        try {
            Account source = Optional.ofNullable(sourceId)
                    .flatMap(accountService::findById)
                    .orElseThrow(() -> new InvalidSourceAccountException(msg.badSourceId));

            Account destination = Optional.ofNullable(destinationId)
                    .flatMap(accountService::findById)
                    .orElseThrow(() -> new InvalidDestinationAccountException(msg.badDestinationId));

            BigDecimal _funds = Optional.ofNullable(funds)
                    .orElseThrow(() -> new NullTransferFundsException(msg.invalidTransferAmount));

            Transfer transfer = Transfer.builder()
                    .funds(_funds)
                    .sourceAccount(source)
                    .destinationAccount(destination)
                    .build();

            transfer(transfer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void transfer(Transfer transfer) {
        validateTransfer(transfer);
        executeTransfer(transfer);
        logTransfer(transfer);
    }

    private void validateTransfer(Transfer transfer) {

        if (transferQuotaExceeded(transfer) ||
                transfer.getFunds().compareTo(MINIMUM_TRANSFER_AMOUNT) < 0) {
            throw new InsufficientTransferFundsException(msg.invalidTransferAmount);
        }

        if (transfer.getSourceAccount().getFunds().compareTo(transfer.getFunds()) < 0) {
            throw new InsufficientSourceFundsException(msg.insufficientFunds);
        }
    }

    private void executeTransfer(Transfer transfer) {

        Supplier<TransferFailedException> transferFailedException = () -> new TransferFailedException(msg.fail);

        transfer.getSourceAccount()
                .setFunds(transfer.getSourceAccount().getFunds().subtract(transfer.getFunds()));

        transfer.getDestinationAccount()
                .setFunds(transfer.getDestinationAccount().getFunds().add(transfer.getFunds()));

        accountService.update(transfer.getSourceAccount())
                .map(_updSrc -> _updSrc.equals(transfer.getSourceAccount()))
                .orElseThrow(transferFailedException);

        accountService.update(transfer.getDestinationAccount())
                .map(_updDst -> _updDst.equals(transfer.getDestinationAccount()))
                .orElseThrow(transferFailedException);
    }

    private void logTransfer(Transfer transfer) {
        transferRepository.save(
                Transfer.builder()
                        .funds(transfer.getFunds())
                        .sourceAccount(transfer.getSourceAccount())
                        .destinationAccount(transfer.getDestinationAccount())
                        .build())
                .orElseThrow(() -> new TransferLoggingException(msg.loggingFail));
    }

    public boolean transferQuotaExceeded(@NotNull Transfer transfer) {
        return transfer.getSourceAccount() != null &&
                transfer.getSourceAccount().getFunds() != null &&
                transfer.getFunds() != null &&
                transfer.getSourceAccount().getFunds().compareTo(transfer.getFunds()) > 0 &&
                transfer.getFunds().divide(transfer.getSourceAccount().getFunds(), FLOOR)
                        .multiply(_100_PERCENT_)
                        .compareTo(ALLOWED_TRANSFER_QUOTA) > 0;
    }

    public Transfer transferFromDTO(@NotNull ITransferDTO dto) {
        Transfer.TransferBuilder builder = Transfer.builder();
        Optional.ofNullable(dto.getFunds()).ifPresent(builder::funds);
        accountService.findById(dto.getSourceId()).ifPresent(builder::sourceAccount);
        accountService.findById(dto.getDestinationId()).ifPresent(builder::destinationAccount);
        return builder.build();
    }
}
