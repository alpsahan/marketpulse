package com.alpsahan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.alpsahan.dto.CreateUserRequest;
import com.alpsahan.dto.UserResponse;
import com.alpsahan.entity.User;
import com.alpsahan.exception.EmailAlreadyExistsException;
import com.alpsahan.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    
    @Test
    void shouldCreateUserSuccessfully() {

        String email = "alp@test.com";
        String rawPassword = "123456";
        String encodedPassword = "$2a$10$fakeHash";

        CreateUserRequest request = new CreateUserRequest();
        request.setEmail(email);
        request.setPassword(rawPassword);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(email);
        savedUser.setPassword(encodedPassword);

        when(userRepository.existsByEmail(email))
                .thenReturn(false);

        when(passwordEncoder.encode(rawPassword))
                .thenReturn(encodedPassword);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        UserResponse response = userService.createUser(request);

        assertEquals(1L, response.getId());
        assertEquals(email, response.getEmail());

        verify(userRepository)
                .existsByEmail(email);

        verify(passwordEncoder)
                .encode(rawPassword);

        verify(userRepository)
                .save(any(User.class));
    }
    
    
    
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        String email = "alp@test.com";

        CreateUserRequest request = new CreateUserRequest();
        request.setEmail(email);
        request.setPassword("123456");

        when(userRepository.existsByEmail(email))
                .thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.createUser(request));

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));
    }
    
    
    
    
}