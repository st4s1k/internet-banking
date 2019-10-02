package com.endava.internship.internetbanking.controllers;

import com.endava.internship.internetbanking.dto.UserDTO;
import com.endava.internship.internetbanking.entities.User;
import com.endava.internship.internetbanking.sevices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Value("user.creation.success")
    private String userCreationSuccess;

    @Value("user.creation.fail")
    private String userCreationFail;

    @Value("user.creation.fail.existing.username")
    private String userCreationFailExistingUsername;

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
            return ResponseEntity.badRequest().body(userCreationFailExistingUsername);
        }

        User userToBeCreated = User.builder()
                .setName(userDTO.getName())
                .build();

        Optional<User> createdUser = userService.createUser(userToBeCreated);

        return createdUser.isPresent() && createdUser.get().equals(userToBeCreated)
                ? ResponseEntity.ok(userCreationSuccess + " [username: " + userDTO.getName() + "]")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(userCreationFail + " [username: " + userDTO.getName() + "]");
    }
}
