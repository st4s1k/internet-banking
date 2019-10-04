package com.endava.internship.internetbanking.controllers;

import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.dto.TransferDTO;
import com.endava.internship.internetbanking.exceptions.*;
import com.endava.internship.internetbanking.services.BankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/banking")
public class BankingController {

    private BankingService bankingService;
    private Messages.Http.Transfer msg;

    @Autowired
    public BankingController(BankingService bankingService,
                             Messages msg) {
        this.bankingService = bankingService;
        this.msg = msg.http.transfer;
    }

    @PutMapping("/topup")
    public ResponseEntity topUp(@Valid @RequestBody TransferDTO dto) {
        ResponseEntity response;
        try {
            // FIXME rewrite without throwing exceptions
            bankingService.topUp(dto.getCurrentAccountId(), dto.getTargetAccountId(), dto.getFunds());
            response = ResponseEntity.ok(msg.success);
        } catch (InvalidDestinationAccountException e) {
            response = ResponseEntity.badRequest().body(msg.targetAccountNotFound);
        } catch (InvalidSourceAccountException e) {
            response = ResponseEntity.badRequest().body(msg.currentAccountNotFound);
        } catch (TransferQuoteExceededException | InsufficientTransferFundsException e) {
            response = ResponseEntity.badRequest().body(msg.invalidTransferAmount);
        } catch (InsufficientSourceFundsException e) {
            response = ResponseEntity.badRequest().body(msg.insufficientFunds);
        }
        return response;
    }

    @PutMapping("/drawdown")
    public ResponseEntity drawDown(@Valid @RequestBody TransferDTO dto) {
        ResponseEntity response;
        try {
            bankingService.drawDown(dto.getCurrentAccountId(), dto.getTargetAccountId(), dto.getFunds());
            response = ResponseEntity.ok(msg.success);
        } catch (InvalidDestinationAccountException e) {
            response = ResponseEntity.badRequest().body(msg.currentAccountNotFound);
        } catch (InvalidSourceAccountException e) {
            response = ResponseEntity.badRequest().body(msg.targetAccountNotFound);
        } catch (TransferQuoteExceededException | InsufficientTransferFundsException e) {
            response = ResponseEntity.badRequest().body(msg.invalidTransferAmount);
        } catch (InsufficientSourceFundsException e) {
            response = ResponseEntity.badRequest().body(msg.insufficientFunds);
        }
        return response;
    }
}
