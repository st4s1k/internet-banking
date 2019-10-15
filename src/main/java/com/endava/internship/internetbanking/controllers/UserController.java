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
    public ResponseEntity<ResponseBean> getAllUsers() {
        List<User> users = userService.findAll();
        return users.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity
                .ok(ResponseBean.builder().status(OK.value()).data(users).build());
    }

    @PostMapping
    public ResponseEntity<ResponseBean> createUser(@RequestBody @Valid UserDTO dto) {

        Optional<User> user = userService.findByName(dto.getName());

        if (user.isPresent()) {
            return ResponseEntity.badRequest()
                    .body(ResponseBean.builder().status(BAD_REQUEST.value())
                            .message(msg.creation.failExistingUsername).data(dto).build());
        }

        User userToBeCreated = new User();
        userToBeCreated.setName(dto.getName());

        Optional<User> createdUser = userService.createUser(userToBeCreated);

        return createdUser.isPresent() && createdUser.get().equals(userToBeCreated)
                ? ResponseEntity.ok(ResponseBean.builder().status(OK.value())
                .message(msg.creation.success).data(createdUser.get().dto()).build())
                : ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ResponseBean.builder().status(INTERNAL_SERVER_ERROR.value())
                        .message(msg.creation.fail).data(dto).build());
    }
}
