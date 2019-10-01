package com.endava.controllers;

import com.endava.dto.TransferDTO;
import com.endava.sevices.BankingService;
import org.springframework.beans.factory.annotation.Autowired;
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
                            return ResponseEntity.ok("Operation executed successfully.");
                        }).orElse(ResponseEntity.badRequest()
                                .body("Could not define receiver account.")))
                .orElse(ResponseEntity.badRequest()
                        .body("Could not define sender account"));
    }
}
