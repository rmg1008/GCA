package com.gca.service;

import com.gca.domain.Role;
import com.gca.domain.User;
import com.gca.repository.RoleRepository;
import com.gca.repository.UserRepository;
import com.gca.service.impl.DefaultUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private DefaultUserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encoded-password";

    private User createUser() {
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        return user;
    }

    @Test
    void testFindByEmailSuccess() {
        User user = createUser();
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        User result = userService.findByEmail(EMAIL);
        assertEquals(EMAIL, result.getEmail());
    }

    @Test
    void testFindByEmailThrowsException() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.findByEmail(EMAIL));
    }

    @Test
    void testSaveUser() {
        User user = createUser();
        Role gestorRole = new Role();
        gestorRole.setName("Gestor");

        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(roleRepository.getRoleByName("Gestor")).thenReturn(List.of(gestorRole));

        userService.save(user);

        assertEquals(ENCODED_PASSWORD, user.getPassword());
        assertTrue(user.getRoles().contains(gestorRole));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testLoadUserByUsernameDelegatesToFindByEmail() {
        User user = createUser();
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertEquals(user, userService.loadUserByUsername(EMAIL));
    }
}