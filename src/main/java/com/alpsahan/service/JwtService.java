package com.alpsahan.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.alpsahan.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class JwtService {
	
	private final JwtEncoder jwtEncoder;
	
	public String generateToken(User user) {
		
		Instant now = Instant.now();
		
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("marketpulse")
				.issuedAt(now)
				.expiresAt(now.plus(1, ChronoUnit.HOURS))
				.subject(user.getEmail())
				.claim("userId", user.getId())
				.build();
		
		JwsHeader header = JwsHeader
				.with(MacAlgorithm.HS256)
				.build();
		
		return jwtEncoder
				.encode(JwtEncoderParameters.from(header, claims))
				.getTokenValue();
	}
}
	
