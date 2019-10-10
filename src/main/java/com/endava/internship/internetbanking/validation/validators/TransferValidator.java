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
        boolean isValid = transferTypeNotNull(context);
        isValid &= dtoNotNull(dto, context);
        isValid &= currentAccountIdNotNull(dto, context);
        isValid &= targetAccountIdNotNull(dto, context);
        isValid &= fundsNotNull(dto, context);
        isValid &= minimumTransferAmountIsMet(dto, context);
        isValid &= transferQuotaIsRespected(dto, context);
        return isValid;
    }

    private boolean transferTypeNotNull(ConstraintValidatorContext context) {
        if (transferType == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.transferObjectNull)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private Optional<Account> sourceAccount(TransferDTO dto) {
        if (dto == null) {
            return Optional.empty();
        }
        switch (transferType) {
            case TOP_UP:
                return accountService.findById(dto.getCurrentAccountId());
            case DRAW_DOWN:
                return accountService.findById(dto.getTargetAccountId());
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

    private boolean fundsNotNull(TransferDTO dto, ConstraintValidatorContext context) {
        if (dto != null && dto.getFunds() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(msg.transferAmountNull)
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
        return dto.getFunds() == null || sourceAccount(dto).map(source -> {
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
