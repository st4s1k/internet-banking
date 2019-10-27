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
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${internetbanking.endpoints.users.url}")
public class UserController {

    private final UserService userService;

    private final Function<List<UserDTO>, ResponseEntity<ResponseBean>> noContentResponse;
    private final Function<List<UserDTO>, Supplier<ResponseEntity<ResponseBean>>> allUsersResponse;
    private final Function<User, ResponseEntity<ResponseBean>> userCreationFailResponse;
    private final Function<User, ResponseEntity<ResponseBean>> userCreationSuccessResponse;
    private final Supplier<ResponseEntity<ResponseBean>> internalServerErrorResponse;

    @Autowired
    public UserController(UserService userService,
                          Messages msg) {
        this.userService = userService;

        this.noContentResponse = u ->
                ResponseEntity.status(NO_CONTENT).body(ResponseBean.builder()
                        .status(NO_CONTENT.value())
                        .message(msg.http.user.noContent)
                        .build());

        this.allUsersResponse = users -> () ->
                ResponseEntity.ok(ResponseBean.builder()
                        .status(OK.value())
                        .data(users)
                        .build());

        this.userCreationFailResponse = existingUser ->
                ResponseEntity.badRequest().body(ResponseBean.builder()
                        .status(BAD_REQUEST.value())
                        .message(msg.http.user.creation.failExistingUsername)
                        .build());

        this.userCreationSuccessResponse = createdUser ->
                ResponseEntity.ok(ResponseBean.builder()
                        .status(OK.value())
                        .message(msg.http.user.creation.success)
                        .data(createdUser.dto())
                        .build());

        this.internalServerErrorResponse = () ->
                ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ResponseBean.builder()
                        .status(INTERNAL_SERVER_ERROR.value())
                        .message(msg.http.user.creation.fail)
                        .build());

    }

    @GetMapping
    public ResponseEntity<ResponseBean> getAllUsers() {
        List<UserDTO> users = userService.findAll().stream()
                .map(User::dto)
                .collect(toList());
        return Optional.of(users)
                .filter(List::isEmpty)
                .map(noContentResponse)
                .orElseGet(allUsersResponse.apply(users));
    }

    @PostMapping
    public ResponseEntity<ResponseBean> createUser(@RequestBody @Valid UserDTO dto) {
        return userService.findByName(dto.getName())
                .map(userCreationFailResponse)
                .orElse(userService.createUser(dto.getName())
                        .map(userCreationSuccessResponse)
                        .orElseGet(internalServerErrorResponse));
    }
}
