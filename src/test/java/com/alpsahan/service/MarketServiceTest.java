package com.alpsahan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alpsahan.client.BinanceClient;
import com.alpsahan.dto.BinancePriceResponse;
import com.alpsahan.dto.MarketPriceResponse;

@ExtendWith(MockitoExtension.class)
public class MarketServiceTest {

    @Mock
    private BinanceClient binanceClient;

    @Mock
    private MarketPriceCacheService cacheService;

    @InjectMocks
    private MarketService marketService;

    
    @Test
    void shouldReturnPriceFromCacheWhenPriceExists() {

        String symbol = "btcusdt";
        BigDecimal cachedPrice = new BigDecimal("85000");

        when(cacheService.getPrice("BTCUSDT"))
                .thenReturn(cachedPrice);

        MarketPriceResponse response = marketService.getPrice(symbol);

        assertEquals("BTCUSDT", response.getSymbol());
        assertEquals(cachedPrice, response.getPrice());

        verify(cacheService)
                .getPrice("BTCUSDT");

        verify(binanceClient, never())
                .getPrice("BTCUSDT");
    }
    
    
    
    @Test
    void shouldFetchPriceFromBinanceAndCacheWhenPriceNotFound() {

        String symbol = "ethusdt";

        BigDecimal binancePrice = new BigDecimal("3500");

        BinancePriceResponse binanceResponse = new BinancePriceResponse(
                        "ETHUSDT",
                        binancePrice
                );

        when(cacheService.getPrice("ETHUSDT"))
                .thenReturn(null);

        when(binanceClient.getPrice("ETHUSDT"))
                .thenReturn(binanceResponse);

        MarketPriceResponse response = marketService.getPrice(symbol);

        assertEquals("ETHUSDT", response.getSymbol());
        assertEquals(binancePrice, response.getPrice());

        verify(cacheService)
                .getPrice("ETHUSDT");

        verify(binanceClient)
                .getPrice("ETHUSDT");

        verify(cacheService)
                .savePrice(
                        "ETHUSDT",
                        binancePrice);
    }
    
    
    
    
}