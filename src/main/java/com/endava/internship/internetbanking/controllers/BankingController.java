package com.endava.internship.internetbanking.controllers;

import com.endava.internship.internetbanking.beans.ResponseBean;
import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.dto.DrawDownDTO;
import com.endava.internship.internetbanking.dto.TopUpDTO;
import com.endava.internship.internetbanking.services.BankingService;
import com.endava.internship.internetbanking.validation.annotations.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequestMapping("${internetbanking.endpoints.banking.url}")
public class BankingController {

    private final TaskExecutor taskExecutor;
    private final BankingService bankingService;
    private final Messages.Http.Transfer msg;

    @Autowired
    public BankingController(TaskExecutor taskExecutor,
                             BankingService bankingService,
                             Messages msg) {
        this.taskExecutor = taskExecutor;
        this.bankingService = bankingService;
        this.msg = msg.http.transfer;
    }

    @PutMapping("${internetbanking.endpoints.banking.top-up}")
    public ResponseEntity<ResponseBean> topUp(@Transfer @RequestBody TopUpDTO dto) {
        taskExecutor.execute(() -> {
        });
        bankingService.topUp(dto.getCurrentAccountId(), dto.getTargetAccountId(), dto.getFunds());
        return ResponseEntity.ok(ResponseBean.builder().status(OK.value()).message(msg.success).build());
    }

    @PutMapping("${internetbanking.endpoints.banking.draw-down}")
    public ResponseEntity<ResponseBean> drawDown(@Transfer @RequestBody DrawDownDTO dto) {
        taskExecutor.execute(() -> {
        });
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
