package com.endava.internship.internetbanking.validation.validators;

import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.dto.ITransferDTO;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.services.AccountService;
import com.endava.internship.internetbanking.services.TransferService;
import com.endava.internship.internetbanking.validation.annotations.Transfer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import static com.endava.internship.internetbanking.services.TransferService.MINIMUM_TRANSFER_AMOUNT;

public class TransferValidator implements ConstraintValidator<Transfer, ITransferDTO> {

    private Messages.Http.Transfer msg;
    private TransferService transferService;
    private AccountService accountService;

    public TransferValidator(Messages msg,
                             TransferService transferService,
                             AccountService accountService) {
        this.msg = msg.http.transfer;
        this.transferService = transferService;
        this.accountService = accountService;
    }

    @Override
    public boolean isValid(ITransferDTO dto, ConstraintValidatorContext context) {
        return validate(dto, context,
                this::dtoNotNull,
                this::currentAccountIdNotNull,
                this::targetAccountIdNotNull,
                this::currentAccountDoesExist,
                this::targetAccountDoesExist,
                this::fundsNotNull,
                this::sufficientSourceFunds,
                this::minimumTransferAmountIsMet,
                this::transferQuotaIsRespected);
    }

    @SafeVarargs
    private final boolean validate(ITransferDTO dto, ConstraintValidatorContext context,
                                   BiPredicate<ITransferDTO, ConstraintValidatorContext>... constraints) {
        boolean isValid = true;
        for (BiPredicate<ITransferDTO, ConstraintValidatorContext> constraint : constraints) {
            isValid &= constraint.test(dto, context);
        }
        return isValid;
    }

    private Optional<Account> getSourceAccount(ITransferDTO dto) {
        return Optional.ofNullable(dto)
                .map(ITransferDTO::getSourceId)
                .flatMap(accountService::findById);
    }

    private Optional<Account> getAccount(ITransferDTO dto, Supplier<Long> idSupplier) {
        if (dto == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(idSupplier.get())
                .flatMap(accountService::findById);
    }

    private Optional<Account> getCurrentAccount(ITransferDTO dto) {
        return getAccount(dto, dto::getCurrentAccountId);
    }

    private Optional<Account> getTargetAccount(ITransferDTO dto) {
        return getAccount(dto, dto::getTargetAccountId);
    }

    private boolean dtoNotNull(ITransferDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.transferObjectNull)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean currentAccountIdNotNull(ITransferDTO dto, ConstraintValidatorContext context) {
        if (dto != null && dto.getCurrentAccountId() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.currentAccountNull)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean targetAccountIdNotNull(ITransferDTO dto, ConstraintValidatorContext context) {
        if (dto != null && dto.getTargetAccountId() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.targetAccountNull)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean currentAccountDoesExist(ITransferDTO dto, ConstraintValidatorContext context) {
        if (!getCurrentAccount(dto).isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.currentAccountNotFound)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean targetAccountDoesExist(ITransferDTO dto, ConstraintValidatorContext context) {
        if (!getTargetAccount(dto).isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.targetAccountNotFound)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean fundsNotNull(ITransferDTO dto, ConstraintValidatorContext context) {
        if (dto != null && dto.getFunds() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.transferAmountNull)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean sufficientSourceFunds(ITransferDTO dto, ConstraintValidatorContext context) {
        if (dto != null && dto.getFunds() != null && getSourceAccount(dto)
                .map(source -> source.getFunds().compareTo(dto.getFunds()) < 0)
                .orElse(false)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.invalidTransferAmount)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean minimumTransferAmountIsMet(ITransferDTO dto, ConstraintValidatorContext context) {
        if (dto != null && dto.getFunds() != null
                && dto.getFunds().compareTo(MINIMUM_TRANSFER_AMOUNT) < 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.invalidTransferAmount)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean transferQuotaIsRespected(ITransferDTO dto, ConstraintValidatorContext context) {
        return dto == null || dto.getFunds() == null ||
                getSourceAccount(dto).map(source -> {
                    if (source.getFunds() != null &&
                            transferService.transferQuotaExceeded(transferService.transferFromDTO(dto))) {
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate(msg.invalidTransferAmount)
                                .addConstraintViolation();
                        return false;
                    }
                    return true;
                }).orElse(true);
    }
}
