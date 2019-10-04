package com.endava.internship.internetbanking.controllers;

import com.endava.internship.internetbanking.dto.TransferDTO;
import com.endava.internship.internetbanking.exceptions.*;
import com.endava.internship.internetbanking.sevices.BankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/banking")
public class BankingController {

    @Value("${http.transfer_operation.success}")
    private String transferOperationSuccessResponse;

    @Value("${http.transfer_operation.fail.bad_current_account}")
    private String currentAccountNotFoundResponse;

    @Value("${http.transfer_operation.fail.bad_target_account}")
    private String targetAccountNotFoundResponse;

    @Value("${http.transfer_operation.fail.bad_transfer_amount}")
    private String badTransferAmountResponse;

    @Value("${http.transfer_operation.fail.insufficient_funds}")
    private String insufficientFundsResponse;

    @Autowired
    private BankingService bankingService;

    @PutMapping("/topup")
    public ResponseEntity topUp(@Valid @RequestBody TransferDTO dto) {
        ResponseEntity response;
        try {
            bankingService.topUp(dto.getCurrentAccountId(), dto.getTargetAccountId(), dto.getFunds());
            response = ResponseEntity.ok(transferOperationSuccessResponse);
        } catch (InvalidDestinationAccountException e) {
            response = ResponseEntity.badRequest().body(targetAccountNotFoundResponse);
        } catch (InvalidSourceAccountException e) {
            response = ResponseEntity.badRequest().body(currentAccountNotFoundResponse);
        } catch (TransferQuoteExceededException | InsufficientTransferFundsException e) {
            response = ResponseEntity.badRequest().body(badTransferAmountResponse);
        } catch (InsufficientSourceFundsException e) {
            response = ResponseEntity.badRequest().body(insufficientFundsResponse);
        }
        return response;
    }

    @PutMapping("/drawdown")
    public ResponseEntity drawDown(@Valid @RequestBody TransferDTO dto) {
        ResponseEntity response;
        try {
            bankingService.drawDown(dto.getCurrentAccountId(), dto.getTargetAccountId(), dto.getFunds());
            response = ResponseEntity.ok(transferOperationSuccessResponse);
        } catch (InvalidSourceAccountException e) {
            response = ResponseEntity.badRequest().body(targetAccountNotFoundResponse);
        } catch (InvalidDestinationAccountException e) {
            response = ResponseEntity.badRequest().body(currentAccountNotFoundResponse);
        } catch (TransferQuoteExceededException | InsufficientTransferFundsException e) {
            response = ResponseEntity.badRequest().body(badTransferAmountResponse);
        } catch (InsufficientSourceFundsException e) {
            response = ResponseEntity.badRequest().body(insufficientFundsResponse);
        }
        return response;
    }
}
