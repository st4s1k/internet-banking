package com.endava.controllers;

import com.endava.entities.User;
import com.endava.sevices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity getAllUsers() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity createUser(@RequestBody @Valid User user) {
        try {
            // TODO: Test SQLExceptions
            return userService.createUser(user)
                    ? new ResponseEntity<>("User " + user.getName() + " successfully created.", HttpStatus.OK)
                    : new ResponseEntity<>("Failed to create user " + user.getName(),
                    HttpStatus.EXPECTATION_FAILED);
        } catch (SQLException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
