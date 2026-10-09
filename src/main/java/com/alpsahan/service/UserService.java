package com.alpsahan.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alpsahan.dto.CreateUserRequest;
import com.alpsahan.dto.UserResponse;
import com.alpsahan.entity.User;
import com.alpsahan.exception.EmailAlreadyExistsException;
import com.alpsahan.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserResponse createUser(CreateUserRequest request) {
		
		if(userRepository.existsByEmail(request.getEmail())) {
			
			throw new EmailAlreadyExistsException();
		}
		
		
		User user = new User();
		
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		
		User savedUser = userRepository.save(user);
		
		return new UserResponse(
				savedUser.getId(),
				savedUser.getEmail());
	}
}
