package com.gca.repositories;


import com.gca.domain.User;
import com.gca.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user1 = new User("Alice", "1234", "alice@test.com");
        userRepository.save(user1);
    }

    @Test
    public void testFindUserByEmailWhenValidEmail(){
        Optional<User> userA = userRepository.findByEmail("alice@test.com");
        Assertions.assertTrue(userA.isPresent());
        Assertions.assertEquals("alice@test.com", userA.get().getEmail());
    }
}
