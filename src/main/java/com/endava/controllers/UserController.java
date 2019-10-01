package com.endava.controllers;

import com.endava.dto.UserDTO;
import com.endava.entities.User;
import com.endava.sevices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity getAllUsers() {
        List<User> users = userService.findAll();
        return users.isEmpty()
                ? ResponseEntity.ok(users)
                : ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity createUser(@RequestBody @Valid UserDTO userDTO) {

        Optional<User> user = userService.findByName(userDTO.getName());

        if (user.isPresent()) {
            return ResponseEntity.badRequest().body("User with this name already exists.");
        }

        User userToBeCreated = User.builder()
                .setName(userDTO.getName())
                .build();

        Optional<User> createdUser = userService.createUser(userToBeCreated);

        return createdUser.isPresent() && createdUser.get().equals(userToBeCreated)
                ? ResponseEntity.ok("User " + userDTO.getName() + " successfully created.")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to create user " + userDTO.getName());
    }
}
