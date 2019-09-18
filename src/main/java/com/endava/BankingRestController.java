package com.endava;

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
        ResponseEntity response = new ResponseEntity(HttpStatus.OK);
        if (userService.findById(id).isPresent()) {
            bankingService.topUp(id, funds);
        } else {
            response = new ResponseEntity(HttpStatus.EXPECTATION_FAILED);
        }
        return response;
    }
}
