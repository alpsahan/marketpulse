package com.alpsahan.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import com.alpsahan.dto.CreatePriceAlertRequest;
import com.alpsahan.dto.PageResponse;
import com.alpsahan.dto.PriceAlertResponse;import com.alpsahan.dto.UpdatePriceAlertRequest;
import com.alpsahan.entity.AlertCondition;
import com.alpsahan.service.PriceAlertService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/alerts")
public class PriceAlertController {
	
	private final PriceAlertService priceAlertService;
	
	
	@PostMapping
	public ResponseEntity<PriceAlertResponse> createAlert(@Valid @RequestBody CreatePriceAlertRequest request, @AuthenticationPrincipal Jwt jwt) {
		
		String email = jwt.getSubject();
		
		PriceAlertResponse response = priceAlertService.createAlert(request, email);
		
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(response);
	}
	
	
	@GetMapping
	public ResponseEntity<PageResponse<PriceAlertResponse>> getAlerts(
			@RequestParam(required = false) String symbol,
			@RequestParam(required = false) Boolean active,
			@RequestParam(required = false) AlertCondition condition,
			@AuthenticationPrincipal Jwt jwt,
			Pageable pageable) {
		
		String email = jwt.getSubject();
		
		Page<PriceAlertResponse> page = priceAlertService.getAlerts(email, symbol, active, condition, pageable);
		
		return ResponseEntity.ok(PageResponse.from(page));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAlert(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
		
		String email = jwt.getSubject();
		
		priceAlertService.deleteAlert(id, email);
		
		return ResponseEntity.noContent().build();
		
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<PriceAlertResponse> updateAlert(
			@PathVariable Long id,
			@Valid @RequestBody UpdatePriceAlertRequest request,
			@AuthenticationPrincipal Jwt jwt) {
		
		String email = jwt.getSubject();
		
		PriceAlertResponse response = priceAlertService.updateAlert(id, email, request);
		
		return ResponseEntity.ok(response);
		
	}
	
}
