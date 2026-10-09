package com.alpsahan.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.alpsahan.client.BinanceClient;
import com.alpsahan.dto.BinancePriceResponse;
import com.alpsahan.dto.MarketPriceResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MarketService {
	
	private final BinanceClient binanceClient;
	private final MarketPriceCacheService cacheService;
	
	
	public MarketPriceResponse getPrice(String symbol) {
		
		
		String normalizedSymbol = symbol.toUpperCase();
		
		BigDecimal cachedPrice = cacheService.getPrice(normalizedSymbol);
		
		if(cachedPrice != null) {
			
			log.info("REDIS HIT - {}", normalizedSymbol);
			
			return new MarketPriceResponse(normalizedSymbol, cachedPrice);
			
		}
		
		log.info("REDIS MISS - {}", normalizedSymbol);
		
		BinancePriceResponse response = binanceClient.getPrice(normalizedSymbol);
		
		cacheService.savePrice(response.getSymbol(), response.getPrice());
		
		
		return new MarketPriceResponse(response.getSymbol(), response.getPrice());
		
	}
	
	
}
