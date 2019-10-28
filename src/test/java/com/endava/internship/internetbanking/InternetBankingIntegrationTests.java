package com.endava.internship.internetbanking;

import com.endava.internship.internetbanking.config.Endpoints;
import com.endava.internship.internetbanking.config.Messages;
import com.endava.internship.internetbanking.dto.AccountDTO;
import com.endava.internship.internetbanking.dto.DrawDownDTO;
import com.endava.internship.internetbanking.dto.TopUpDTO;
import com.endava.internship.internetbanking.dto.UserDTO;
import com.endava.internship.internetbanking.entities.Account;
import com.endava.internship.internetbanking.services.AccountService;
import com.endava.internship.internetbanking.services.UserService;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class InternetBankingIntegrationTests {

    private static Set<Long> usersCache = new HashSet<>();
    private static Set<Long> accountsCache = new HashSet<>();

    @LocalServerPort
    private int port;

    private static UserService userService;
    private static AccountService accountService;

    private Messages msg;
    private Endpoints endpoints;

    @Autowired
    public InternetBankingIntegrationTests(UserService userService,
                                           AccountService accountService,
                                           Messages msg,
                                           Endpoints endpoints) {

        InternetBankingIntegrationTests.userService = userService;
        InternetBankingIntegrationTests.accountService = accountService;

        this.msg = msg;
        this.endpoints = endpoints;
    }

    private Response createUser(String name) {
        Response response = given().port(port).when()
                .log().ifValidationFails()
                .header("Content-Type", "application/json")
                .body(new UserDTO(name))
                .post(endpoints.users.url);
        usersCache.add(response.body().jsonPath().getLong("data.id"));
        return response;
    }

    private Response createAccount(Long userId) {
        Response response = given().port(this.port).when()
                .log().ifValidationFails()
                .header("Content-Type", "application/json")
                .body(new AccountDTO(userId))
                .post(endpoints.accounts.url);
        accountsCache.add(response.body().jsonPath().getLong("data.id"));
        return response;
    }

    @AfterAll
    public static void cleanUp() {
        accountsCache.stream()
                .map(accountService::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(accountService::remove);
        usersCache.stream()
                .map(userService::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(userService::remove);
    }

    @Test
    public void userControllerTest() {

        String userName = "IT TestUser One";

        assertFalse(userService.findByName(userName).isPresent());

        Response response = createUser(userName);
        UserDTO createdUserDTO = response.jsonPath().getObject("data", UserDTO.class);

        assertTrue(userService.findByName(userName).isPresent());

        response.then().assertThat()
                .log().ifValidationFails()
                .statusCode(OK.value())
                .contentType(JSON)
                .body(notNullValue())
                .body("timestamp", notNullValue())
                .body("status", equalTo(OK.value()))
                .body("message", equalTo(msg.http.user.creation.success));

        assertNotNull(createdUserDTO.getId());
        assertEquals(userName, createdUserDTO.getName());
    }

    @Test
    public void accountControllerTest() {

        Response response = createUser("IT TestUser Two");
        UserDTO createdUserDTO = response.jsonPath().getObject("data", UserDTO.class);

        assertTrue(accountService.findByUserId(createdUserDTO.getId()).isEmpty());

        response = createAccount(createdUserDTO.getId());
        AccountDTO createdAccountDTO = response.getBody().jsonPath()
                .getObject("data", AccountDTO.class);

        assertFalse(accountService.findByUserId(createdUserDTO.getId()).isEmpty());

        response.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(OK.value())
                .contentType(JSON)
                .body(notNullValue())
                .body("timestamp", notNullValue())
                .body("status", equalTo(OK.value()))
                .body("message", equalTo(msg.http.account.creation.success));

        assertNotNull(createdAccountDTO.getId());
        assertEquals(ZERO, createdAccountDTO.getFunds());
        assertEquals(createdUserDTO.getId(), createdAccountDTO.getUserId());
    }

    @Test
    public void bankingControllerTopUpTest() {

        Response response = createUser("IT TestUser Three");
        UserDTO createdUserDTO = response.jsonPath().getObject("data", UserDTO.class);

        response = createAccount(createdUserDTO.getId());
        AccountDTO createdCurrentAccountDTO = response.getBody().jsonPath()
                .getObject("data", AccountDTO.class);
        Account createdCurrentAccount = accountService.accountFromDTO(createdCurrentAccountDTO);
        createdCurrentAccount.setFunds(TEN.multiply(TEN));
        accountService.update(createdCurrentAccount)
                .map(Account::getFunds)
                .ifPresent(createdCurrentAccount::setFunds);

        response = createAccount(createdUserDTO.getId());
        AccountDTO createdTargetAccountDTO = response.getBody().jsonPath()
                .getObject("data", AccountDTO.class);

        Account createdTargetAccount = accountService.accountFromDTO(createdTargetAccountDTO);

        TopUpDTO topUpDTO = new TopUpDTO(
                createdCurrentAccount.getId(),
                createdTargetAccount.getId(),
                TEN);

        Response topUpResponse = given().port(port)
                .log().ifValidationFails()
                .header("Content-Type", "application/json")
                .body(topUpDTO)
                .put(endpoints.banking.url + endpoints.banking.topUp);

        Optional<Account> optCurrentAccount = accountService.findById(createdCurrentAccount.getId());
        Optional<Account> optTargetAccount = accountService.findById(createdTargetAccount.getId());

        assertTrue(optCurrentAccount.isPresent());
        assertTrue(optTargetAccount.isPresent());

        assertEquals(new BigDecimal(90), optCurrentAccount.get().getFunds());
        assertEquals(new BigDecimal(10), optTargetAccount.get().getFunds());

        topUpResponse.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(OK.value())
                .body(notNullValue())
                .body("status", equalTo(OK.value()))
                .body("message", equalTo(msg.http.transfer.success));
    }

    @Test
    public void bankingControllerDrawDownTest() {

        Response response = createUser("IT TestUser Four");
        UserDTO createdUserDTO = response.jsonPath().getObject("data", UserDTO.class);

        response = createAccount(createdUserDTO.getId());
        AccountDTO createdCurrentAccountDTO = response.getBody().jsonPath()
                .getObject("data", AccountDTO.class);
        Account createdCurrentAccount = accountService.accountFromDTO(createdCurrentAccountDTO);

        response = createAccount(createdUserDTO.getId());
        AccountDTO createdTargetAccountDTO = response.getBody().jsonPath()
                .getObject("data", AccountDTO.class);
        Account createdTargetAccount = accountService.accountFromDTO(createdTargetAccountDTO);
        createdTargetAccount.setFunds(TEN.multiply(TEN));
        accountService.update(createdTargetAccount)
                .map(Account::getFunds)
                .ifPresent(createdTargetAccount::setFunds);

        DrawDownDTO drawDownDTO = new DrawDownDTO(
                createdCurrentAccount.getId(),
                createdTargetAccount.getId(),
                TEN);

        Response drawDownResponse = given().port(port)
                .log().ifValidationFails()
                .header("Content-Type", "application/json")
                .body(drawDownDTO)
                .put(endpoints.banking.url + endpoints.banking.drawDown);

        Optional<Account> optCurrentAccount = accountService.findById(createdCurrentAccount.getId());
        Optional<Account> optTargetAccount = accountService.findById(createdTargetAccount.getId());

        assertTrue(optCurrentAccount.isPresent());
        assertTrue(optTargetAccount.isPresent());

        assertEquals(new BigDecimal(10), optCurrentAccount.get().getFunds());
        assertEquals(new BigDecimal(90), optTargetAccount.get().getFunds());

        drawDownResponse.then()
                .log().ifValidationFails()
                .assertThat()
                .statusCode(OK.value())
                .body(notNullValue())
                .body("status", equalTo(OK.value()))
                .body("message", equalTo(msg.http.transfer.success));
    }
}
