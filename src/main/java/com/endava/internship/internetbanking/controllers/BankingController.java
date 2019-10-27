package com.endava.internship.internetbanking.controllers;

import com.endava.internship.internetbanking.beans.ResponseBean;
import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.dto.DrawDownDTO;
import com.endava.internship.internetbanking.dto.TopUpDTO;
import com.endava.internship.internetbanking.services.BankingService;
import com.endava.internship.internetbanking.validation.annotations.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequestMapping("${internetbanking.endpoints.banking.url}")
public class BankingController {

    private final BankingService bankingService;

    private final Supplier<ResponseEntity<ResponseBean>> successfulTransferResponse;
    private final Function<ConstraintViolationException, ResponseEntity<ResponseBean>> constraintViolationResponse;

    @Autowired
    public BankingController(BankingService bankingService,
                             Messages msg) {
        this.bankingService = bankingService;

        this.successfulTransferResponse = () ->
                ResponseEntity.ok(ResponseBean.builder()
                        .status(OK.value())
                        .message(msg.http.transfer.success)
                        .build());

        this.constraintViolationResponse = e ->
                ResponseEntity.badRequest().body(ResponseBean.builder()
                        .status(BAD_REQUEST.value())
                        .message(e.getConstraintViolations().stream()
                                .map(ConstraintViolation::getMessage)
                                .toArray(String[]::new))
                        .build());

    }

    @PutMapping("${internetbanking.endpoints.banking.top-up}")
    public ResponseEntity<ResponseBean> topUp(@Transfer @RequestBody TopUpDTO dto) {
        bankingService.topUp(dto.getCurrentAccountId(), dto.getTargetAccountId(), dto.getFunds());
        return successfulTransferResponse.get();
    }

    @PutMapping("${internetbanking.endpoints.banking.draw-down}")
    public ResponseEntity<ResponseBean> drawDown(@Transfer @RequestBody DrawDownDTO dto) {
        bankingService.drawDown(dto.getCurrentAccountId(), dto.getTargetAccountId(), dto.getFunds());
        return successfulTransferResponse.get();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseBean> handleConstraintViolationException(ConstraintViolationException e) {
        return constraintViolationResponse.apply(e);
    }
}
