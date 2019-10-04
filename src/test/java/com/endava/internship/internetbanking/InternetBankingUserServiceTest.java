package com.endava.internship.internetbanking;

import com.endava.internship.internetbanking.entities.User;
import com.endava.internship.internetbanking.repositories.UserRepository;
import com.endava.internship.internetbanking.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class InternetBankingUserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testCreateUser() {
        Optional<User> mockUser = Optional.of(new User(12L, "Test User"));
        when(userRepository.save(null)).thenReturn(mockUser);
        assertEquals(userRepository.save(null), mockUser);
    }

    @Test
    public void testFindUserById() {
        Optional<User> optMockUser = Optional.of(new User(1L, "Boring User"));
        when(userRepository.findById(1L)).thenReturn(optMockUser);
        Optional<User> optFoundUser = userService.findById(1L);
        assertTrue(optFoundUser.isPresent());
        assertEquals(optFoundUser.get(), optMockUser.get());
    }
}
