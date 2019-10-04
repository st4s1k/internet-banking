package com.endava.internship.internetbanking.controllers;

import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.dto.UserDTO;
import com.endava.internship.internetbanking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    private Messages.Http.User msg;

    @Autowired
    public UserController(UserService userService,
                          Messages msg) {
        this.userService = userService;
        this.msg = msg.http.user;
    }

    @GetMapping
    public ResponseEntity getAllUsers() {
        List<com.endava.internship.internetbanking.entities.User> users = userService.findAll();
        return users.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(users);
    }

    @PutMapping
    public ResponseEntity createUser(@RequestBody @Valid UserDTO dto) {

        Optional<com.endava.internship.internetbanking.entities.User> user = userService.findByName(dto.getName());

        if (user.isPresent()) {
            return ResponseEntity.badRequest().body(msg.creation.failExistingUsername);
        }

        com.endava.internship.internetbanking.entities.User userToBeCreated = new com.endava.internship.internetbanking.entities.User();
        userToBeCreated.setName(dto.getName());

        Optional<com.endava.internship.internetbanking.entities.User> createdUser = userService.createUser(userToBeCreated);

        return createdUser.isPresent() && createdUser.get().equals(userToBeCreated)
                ? ResponseEntity.ok(createdUser.get().dto()) // msg.userCreation.success
                : ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(msg.creation.fail + " [username: " + dto.getName() + "]");
    }
}
