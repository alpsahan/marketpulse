package com.alpsahan.service;

import java.math.BigDecimal;
import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MarketPriceCacheService {
	
	private static final Duration PRICE_TTL = Duration.ofSeconds(5);
	
	private final StringRedisTemplate redisTemplate;
	
	
	public BigDecimal getPrice(String symbol) {
		
		String key = createKey(symbol);
		
		String cachedPrice = redisTemplate.opsForValue().get(key);
		
		if(cachedPrice == null) {
			return null;
		}
		
		return new BigDecimal(cachedPrice);
		
	}
	
	
	public void savePrice(String symbol, BigDecimal price) {
		
		String key = createKey(symbol);
		
		redisTemplate.opsForValue().set(
				key,
				price.toString(),
				PRICE_TTL);
	}
	
	
	private String createKey(String symbol) {
		return "market:price" + symbol;
	}

}
