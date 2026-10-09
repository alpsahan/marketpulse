package com.alpsahan.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alpsahan.dto.AuthResponse;
import com.alpsahan.dto.LoginRequest;
import com.alpsahan.entity.User;
import com.alpsahan.exception.InvalidCredentialsException;
import com.alpsahan.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	
	
	public AuthResponse login(LoginRequest request) {
		
        User user = userRepository
        		.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);
		
        
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        
        if(!passwordMatches) {
        	throw new InvalidCredentialsException();
        }
        
        String token = jwtService.generateToken(user);
        
        return new AuthResponse(token);
	}
}
