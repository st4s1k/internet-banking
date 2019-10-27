package com.endava.internship.internetbanking.services;

import com.endava.internship.internetbanking.dto.UserDTO;
import com.endava.internship.internetbanking.entities.User;
import com.endava.internship.internetbanking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(REQUIRES_NEW)
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional(REQUIRES_NEW)
    public User createUser(String name) {
        return userRepository.save(new User(name));
    }

    @Transactional(REQUIRES_NEW)
    public Optional<User> update(User user) {
        return userRepository.update(user);
    }

    @Transactional(REQUIRES_NEW)
    public Optional<User> remove(User user) {
        return userRepository.remove(user);
    }

    @Transactional(REQUIRES_NEW)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(REQUIRES_NEW)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(REQUIRES_NEW)
    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Transactional(REQUIRES_NEW)
    public User userFromDTO(@NotNull UserDTO dto) {
        return userRepository.findById(dto.getId())
                .orElse(User.builder()
                        .id(dto.getId())
                        .name(dto.getName())
                        .build());
    }
}
