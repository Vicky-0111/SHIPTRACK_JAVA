package com.shiptrack.service;

import com.shiptrack.dto.LoginRequest;
import com.shiptrack.dto.RegisterRequest;
import com.shiptrack.entity.Role;
import com.shiptrack.entity.User;
import com.shiptrack.exception.InvalidCredentialsException;
import com.shiptrack.exception.UserAlreadyExistsException;
import com.shiptrack.exception.UserNotFoundException;
import com.shiptrack.repository.RoleRepository;
import com.shiptrack.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void loginThrowsUserNotFoundWhenAccountDoesNotExist() {
        LoginRequest request = new LoginRequest();
        request.setEmail("missing@example.com");
        request.setPassword("secret123");

        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.login(request));

        assertEquals(
                "You don't have an account. Please register first.",
                exception.getMessage());
    }

    @Test
    void loginThrowsInvalidCredentialsWhenPasswordIsWrong() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("wrong-password");

        Role role = new Role("ROLE_CUSTOMER");
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encoded-password");
        user.setRole(role);

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password"))
                .thenReturn(false);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request));

        assertEquals(
                "Enter correct email or password.",
                exception.getMessage());
    }

    @Test
    void registerCreatesCustomerAccountWhenEmailIsNew() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("New User");
        request.setEmail("new@example.com");
        request.setPassword("secret123");
        request.setPhone("9999999999");
        request.setRole("ROLE_CUSTOMER");

        Role customerRole = new Role("ROLE_CUSTOMER");

        when(userRepository.findByEmail("new@example.com"))
                .thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_CUSTOMER"))
                .thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("secret123"))
                .thenReturn("encoded-secret");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.register(request);

        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals("encoded-secret", savedUser.getPassword());
        assertEquals("ROLE_CUSTOMER", savedUser.getRole().getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerThrowsWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Existing User");
        request.setEmail("existing@example.com");
        request.setPassword("secret123");

        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(new User()));

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.register(request));

        assertEquals(
                "An account with this email already exists. Please log in.",
                exception.getMessage());
    }
}
