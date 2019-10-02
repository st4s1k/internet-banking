package com.endava.internship.internetbanking.controllers;

import com.endava.internship.internetbanking.dto.TransferDTO;
import com.endava.internship.internetbanking.sevices.BankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Consumer;

@RestController
@RequestMapping("/banking")
public class BankingController {

    @Value("transfer.operation.success")
    private String transferOperationSuccess;

    @Value("transfer.operation.fail.bad.source")
    private String transferOperationFailBadSource;

    @Value("transfer.operation.fail.bad.destination")
    private String transferOperationFailBadDestination;

    @Autowired
    private BankingService bankingService;

    @PutMapping("/topup")
    public ResponseEntity topUp(@Valid @RequestBody TransferDTO transferDTO) {

        return transfer(transferDTO, dto -> bankingService.topUp(
                dto.getSourceId(), dto.getDestinationId(), dto.getFunds()));
    }

    @PutMapping("/drawdown")
    public ResponseEntity drawDown(@Valid @RequestBody TransferDTO transferDTO) {
        return transfer(transferDTO, dto -> bankingService.drawDown(
                dto.getSourceId(), dto.getDestinationId(), dto.getFunds()));
    }

    private ResponseEntity transfer(TransferDTO transferDTO, Consumer<TransferDTO> operation) {
        return Optional.ofNullable(transferDTO.getSourceId())
                .map(src -> Optional.ofNullable(transferDTO.getDestinationId())
                        .map(dst -> {
                            operation.accept(transferDTO);
                            return ResponseEntity.ok(transferOperationSuccess);
                        }).orElse(ResponseEntity.badRequest()
                                .body(transferOperationFailBadDestination)))
                .orElse(ResponseEntity.badRequest()
                        .body(transferOperationFailBadSource));
    }
}
