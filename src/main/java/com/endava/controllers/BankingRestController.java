package com.endava.controllers;

import com.endava.sevices.BankingService;
import com.endava.sevices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/banking")
public class BankingRestController {

    @Autowired
    private BankingService bankingService;

    @Autowired
    private UserService userService;

    @PutMapping("/topup/{id}")
    public ResponseEntity topUp(@PathVariable Long id, @RequestParam Double funds) {
        return userService.findById(id)
                .filter(user -> bankingService.topUp(user.getId(), funds))
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED));
    }
}
