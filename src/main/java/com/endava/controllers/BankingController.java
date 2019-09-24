package com.endava.controllers;

import com.endava.sevices.BankingService;
import com.endava.sevices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/banking")
public class BankingController {

    @Autowired
    private BankingService bankingService;

    @Autowired
    private UserService userService;

    // TODO: top-up account -> account
    @PutMapping("/topup/{id}")
    public ResponseEntity topUp(
            @PathVariable Long id,
            @RequestParam BigDecimal funds) {
        return userService.findById(id)
                .filter(user -> bankingService.topUp(user.getId(), funds))
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED));
    }
}
