package com.endava.internship.internetbanking;

import com.endava.internship.internetbanking.config.Endpoints;
import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.dto.AccountDTO;
import com.endava.internship.internetbanking.dto.UserDTO;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.entities.User;
import com.endava.internship.internetbanking.repositories.AccountRepository;
import com.endava.internship.internetbanking.repositories.UserRepository;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Transactional
public class InternetBankingControllerFullIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private Messages msg;
    @Autowired
    private Endpoints endpoints;
    @LocalServerPort
    private int port;

    @Test
    public void userControllerTest() {

        UserDTO userDTO;

        userDTO = new User("Test User").dto();
        given().port(port)
                .when()
                    .header("Content-Type", "application/json")
                    .body(userDTO)
                    .put(endpoints.users.url)
                .then()
                    .assertThat()
                    .statusCode(OK.value())
                    .contentType(JSON)
                    .body(notNullValue())
                    .body("data[0].name", equalTo(userDTO.getName()))
                    .body("data[0].id", notNullValue())
                    .body("message", equalTo(msg.http.user.creation.success));

        userRepository
                .findByName(userDTO.getName())
                .ifPresent(userRepository::remove);
    }

    @Test
    public void accountControllerTest() {

        UserDTO userDTO = given().port(port)
                    .header("Content-Type", "application/json")
                    .body(new User("Test User").dto())
                    .put(endpoints.users.url)
                .then()
                    .extract()
                    .body()
                    .path("data");

        @NonNull
        @NotNull
        User user = User.from(userDTO);

        AccountDTO accountDTO = new Account(user).dto();

        given().port(port)
                .when()
                    .header("Content-Type", "application/json")
                    .body(accountDTO)
                    .put(endpoints.accounts.url)
                .then()
                    .assertThat()
                    .statusCode(OK.value())
                    .contentType(JSON)
                    .body(notNullValue())
                    .body("data[0].name", equalTo(userDTO.getName()))
                    .body("data[0].id", notNullValue())
                    .body("message", equalTo(msg.http.account.creation.success));

        userRepository
                .findByName(userDTO.getName())
                .ifPresent(userRepository::remove);

        accountRepository
                .findByUserId(accountDTO.getUserId())
                .forEach(accountRepository::remove);
    }
}
