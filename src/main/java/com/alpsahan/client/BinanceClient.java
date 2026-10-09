package com.alpsahan.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.alpsahan.dto.BinancePriceResponse;
import com.alpsahan.exception.InvalidSymbolException;

@Component
public class BinanceClient {
	
	
	private final RestClient restClient;
	
	public BinanceClient(RestClient.Builder builder) {
		
		this.restClient = builder
				.baseUrl("https://data-api.binance.vision")
				.build();
		
	}
	
	public BinancePriceResponse getPrice(String symbol) {
		
		try {
			
			return restClient.get()
	                .uri(uriBuilder -> uriBuilder
	                        .path("/api/v3/ticker/price")
	                        .queryParam("symbol", symbol)
	                        .build())
	                .retrieve()
	                .body(BinancePriceResponse.class);
			
		} catch (HttpClientErrorException e) {
			
			throw new InvalidSymbolException(symbol);
		}
		
		  
		
	}
}
