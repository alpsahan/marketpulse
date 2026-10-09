package com.alpsahan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.alpsahan.dto.AuthResponse;
import com.alpsahan.dto.LoginRequest;
import com.alpsahan.entity.User;
import com.alpsahan.exception.InvalidCredentialsException;
import com.alpsahan.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    
    @Test
    void shouldLoginSuccessfully() {

        String email = "alp@test.com";
        String rawPassword = "123456";
        String encodedPassword = "$2a$10$fakeHash";
        String token = "fake-jwt-token";

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(rawPassword);

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword(encodedPassword);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                rawPassword,
                encodedPassword
        )).thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn(token);

        AuthResponse response = authService.login(request);

        assertEquals(
                "fake-jwt-token",
                response.getToken());

        verify(userRepository).findByEmail(email);

        verify(passwordEncoder).matches(rawPassword, encodedPassword);

        verify(jwtService).generateToken(user);
    }
    
    
    
    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {

        String email = "alp@test.com";
        String rawPassword = "wrongPassword";
        String encodedPassword = "$2a$10$fakeHash";

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(rawPassword);

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword(encodedPassword);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                rawPassword,
                encodedPassword
        )).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(request));

        verify(userRepository).findByEmail(email);

        verify(passwordEncoder).matches(rawPassword, encodedPassword);

        verify(jwtService, never()).generateToken(any(User.class));
    }
    
    
    
    @Test
    void shouldThrowExceptionWhenUserNotFoundWhileLogin() {

        String email = "unknown@test.com";

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword("123456");

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(request));

        verify(userRepository).findByEmail(email);

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtService, never())
                .generateToken(any(User.class));
    }
    
    
    
}