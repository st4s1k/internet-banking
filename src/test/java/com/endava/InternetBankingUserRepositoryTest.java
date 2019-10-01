package com.endava;

import com.endava.entities.User;
import com.endava.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class InternetBankingUserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateAndRetrieveUser() {
        User user = new User.Builder().setName("Mock User").build();

        Optional<User> optSavedUser = userRepository.save(user);
        assertTrue(optSavedUser.isPresent());

        Optional<User> optFoundUser = userRepository.findById(optSavedUser.get().getId());
        assertTrue(optFoundUser.isPresent());

        assertEquals(optSavedUser.get(), optFoundUser.get());

        Optional<User> removedUser = userRepository.remove(optFoundUser.get());
        assertTrue(removedUser.isPresent());

        assertEquals(removedUser.get(), optFoundUser.get());

        removedUser = userRepository.findById(removedUser.get().getId());
        assertFalse(removedUser.isPresent());
    }
}
