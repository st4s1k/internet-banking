package com.endava.internship.internetbanking.controllers;

import com.endava.internship.internetbanking.beans.ResponseBean;
import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.dto.TransferDTO;
import com.endava.internship.internetbanking.services.BankingService;
import com.endava.internship.internetbanking.validation.annotations.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import static com.endava.internship.internetbanking.enums.TransferType.DRAW_DOWN;
import static com.endava.internship.internetbanking.enums.TransferType.TOP_UP;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequestMapping("${endpoints.banking.url}")
public class BankingController {

    private final BankingService bankingService;
    private final Messages.Http.Transfer msg;

    @Autowired
    public BankingController(BankingService bankingService,
                             Messages msg) {
        this.bankingService = bankingService;
        this.msg = msg.http.transfer;
    }

    @PutMapping("${endpoints.banking.top-up}")
    public ResponseEntity<ResponseBean> topUp(@Transfer(TOP_UP)
                                              @RequestBody TransferDTO dto) {
        bankingService.topUp(dto.getCurrentAccountId(), dto.getTargetAccountId(), dto.getFunds());
        return ResponseEntity.ok(ResponseBean.builder().status(OK.value()).message(msg.success).build());
    }

    @PutMapping("${endpoints.banking.draw-down}")
    public ResponseEntity<ResponseBean> drawDown(@Transfer(DRAW_DOWN)
                                                 @RequestBody TransferDTO dto) {
        bankingService.drawDown(dto.getCurrentAccountId(), dto.getTargetAccountId(), dto.getFunds());
        return ResponseEntity.ok(ResponseBean.builder().status(OK.value()).message(msg.success).build());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ResponseBean> handleConstraintViolationException(ConstraintViolationException e) {

        ResponseBean.ResponseBeanBuilder response = ResponseBean.builder();

        String[] violations = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .toArray(String[]::new);

        response.status(BAD_REQUEST.value())
                .message(violations);

        return ResponseEntity.badRequest().body(response.build());
    }
}
