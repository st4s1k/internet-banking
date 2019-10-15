package com.endava.internship.internetbanking.validation.validators;

import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.dto.TransferDTO;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.enums.TransferType;
import com.endava.internship.internetbanking.services.AccountService;
import com.endava.internship.internetbanking.services.BankingService;
import com.endava.internship.internetbanking.validation.annotations.Transfer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import static com.endava.internship.internetbanking.services.BankingService.MINIMUM_TRANSFER_AMOUNT;

public class TransferValidator implements ConstraintValidator<Transfer, TransferDTO> {

    private Messages.Http.Transfer msg;
    private BankingService bankingService;
    private AccountService accountService;
    private TransferType transferType;

    public TransferValidator(Messages msg,
                             BankingService bankingService,
                             AccountService accountService) {
        this.bankingService = bankingService;
        this.msg = msg.http.transfer;
        this.accountService = accountService;
    }

    @Override
    public void initialize(Transfer constraintAnnotation) {
        transferType = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(TransferDTO dto, ConstraintValidatorContext context) {
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
    private final boolean validate(TransferDTO dto, ConstraintValidatorContext context,
                                   BiPredicate<TransferDTO, ConstraintValidatorContext>... constraints) {
        boolean isValid = true;
        for (BiPredicate<TransferDTO, ConstraintValidatorContext> constraint : constraints) {
            isValid &= constraint.test(dto, context);
        }
        return isValid;
    }

    private Optional<Account> getAccount(TransferDTO dto, Supplier<Long> idSupplier) {
        if (dto == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(idSupplier.get())
                .flatMap(accountService::findById);
    }

    private Optional<Account> getCurrentAccount(TransferDTO dto) {
        return getAccount(dto, dto::getCurrentAccountId);
    }

    private Optional<Account> getTargetAccount(TransferDTO dto) {
        return getAccount(dto, dto::getTargetAccountId);
    }

    private Optional<Account> getSourceAccount(TransferDTO dto) {
        if (dto == null) {
            return Optional.empty();
        }
        switch (transferType) {
            case TOP_UP:
                return getCurrentAccount(dto);
            case DRAW_DOWN:
                return getTargetAccount(dto);
            default:
                return Optional.empty();
        }
    }

    private boolean dtoNotNull(TransferDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.transferObjectNull)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean currentAccountIdNotNull(TransferDTO dto, ConstraintValidatorContext context) {
        if (dto != null && dto.getCurrentAccountId() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.currentAccountNull)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean targetAccountIdNotNull(TransferDTO dto, ConstraintValidatorContext context) {
        if (dto != null && dto.getTargetAccountId() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.targetAccountNull)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean currentAccountDoesExist(TransferDTO dto, ConstraintValidatorContext context) {
        if (!getCurrentAccount(dto).isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.currentAccountNotFound)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean targetAccountDoesExist(TransferDTO dto, ConstraintValidatorContext context) {
        if (!getTargetAccount(dto).isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.targetAccountNotFound)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean fundsNotNull(TransferDTO dto, ConstraintValidatorContext context) {
        if (dto != null && dto.getFunds() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.transferAmountNull)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean sufficientSourceFunds(TransferDTO dto, ConstraintValidatorContext context) {
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

    private boolean minimumTransferAmountIsMet(TransferDTO dto, ConstraintValidatorContext context) {
        if (dto != null && dto.getFunds() != null
                && dto.getFunds().compareTo(MINIMUM_TRANSFER_AMOUNT) < 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.invalidTransferAmount)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean transferQuotaIsRespected(TransferDTO dto, ConstraintValidatorContext context) {
        return dto == null || dto.getFunds() == null ||
                getSourceAccount(dto).map(source -> {
                    if (source.getFunds() != null && bankingService.transferQuotaExceeded(source, dto.getFunds())) {
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate(msg.invalidTransferAmount)
                                .addConstraintViolation();
                        return false;
                    }
                    return true;
                }).orElse(true);
    }
}
