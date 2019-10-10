package com.endava.internship.internetbanking.controllers;

import com.endava.internship.internetbanking.beans.ResponseBean;
import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.dto.UserDTO;
import com.endava.internship.internetbanking.entities.User;
import com.endava.internship.internetbanking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${endpoints.users.url}")
public class UserController {

    private final UserService userService;
    private final Messages.Http.User msg;

    @Autowired
    public UserController(UserService userService,
                          Messages msg) {
        this.userService = userService;
        this.msg = msg.http.user;
    }

    @GetMapping
    public ResponseEntity getAllUsers() {
        List<User> users = userService.findAll();
        return users.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity createUser(@RequestBody @Valid UserDTO dto) {

        Optional<User> user = userService.findByName(dto.getName());

        if (user.isPresent()) {
            return ResponseEntity.badRequest()
                    .body(ResponseBean.from(BAD_REQUEST, msg.creation.failExistingUsername, dto));
        }

        User userToBeCreated = new User();
        userToBeCreated.setName(dto.getName());

        Optional<User> createdUser = userService.createUser(userToBeCreated);

        return createdUser.isPresent() && createdUser.get().equals(userToBeCreated)
                ? ResponseEntity.ok(ResponseBean.from(OK, msg.creation.success, createdUser.get().dto()))
                : ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ResponseBean.from(INTERNAL_SERVER_ERROR, msg.creation.fail, dto));
    }
}
