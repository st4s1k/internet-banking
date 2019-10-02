package com.endava.internship.internetbanking;

import com.endava.internship.internetbanking.entities.User;
import com.endava.internship.internetbanking.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Transactional
public class InternetBankingUserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateAndRetrieveUser() {
        User user = User.builder().setName("Mock User").build();

        Optional<User> optSavedUser = userRepository.save(user);
        assertTrue(optSavedUser.isPresent());

        Optional<User> optFoundUser = userRepository.findById(optSavedUser.get().getId());
        assertTrue(optFoundUser.isPresent());

        assertEquals(optSavedUser.get(), optFoundUser.get());
    }
}
