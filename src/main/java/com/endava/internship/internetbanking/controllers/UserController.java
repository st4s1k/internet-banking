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

    @Value("${http.user_creation.success}")
    private String userCreationSuccessResponse;

    @Value("${http.user_creation.fail}")
    private String userCreationFailResponse;

    @Value("${http.user_creation.fail.existing_username}")
    private String userCreationFailExistingUsernameResponse;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity getAllUsers() {
        List<User> users = userService.findAll();
        return users.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(users);
    }

    @PutMapping
    public ResponseEntity createUser(@RequestBody @Valid UserDTO dto) {

        Optional<User> user = userService.findByName(dto.getName());

        if (user.isPresent()) {
            return ResponseEntity.badRequest().body(userCreationFailExistingUsernameResponse);
        }

        User userToBeCreated = new User();
        userToBeCreated.setName(dto.getName());

        Optional<User> createdUser = userService.createUser(userToBeCreated);

        return createdUser.isPresent() && createdUser.get().equals(userToBeCreated)
                ? ResponseEntity.ok(createdUser.get().dto())
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(userCreationFailResponse + " [username: " + dto.getName() + "]");
    }
}
