package com.endava.internship.internetbanking;

import com.endava.internship.internetbanking.entities.User;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
public class InternetBankingControllersTest {

    @Autowired
    private MockMvc mockMvc;


    //use rest assured to make http calls
    @Test
    public void testCreateRetrieveUser() throws Exception {
        User mockUser = new User("Bob Marley");
        String jsonUser = new Gson().toJson(mockUser);
        mockMvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(content().json(jsonUser));
        mockMvc.perform(get("/users")).andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("")));
    }
}
