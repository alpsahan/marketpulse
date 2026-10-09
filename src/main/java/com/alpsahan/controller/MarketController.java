package com.alpsahan.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alpsahan.dto.MarketPriceResponse;
import com.alpsahan.service.MarketService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/market")
@RestController
public class MarketController {
	
	private final MarketService marketService;

	@GetMapping("/prices/{symbol}")
	public MarketPriceResponse getPrice(@PathVariable String symbol) {
		
		return marketService.getPrice(symbol);
	}
}
