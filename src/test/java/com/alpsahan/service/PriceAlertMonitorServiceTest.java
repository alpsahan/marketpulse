package com.alpsahan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alpsahan.dto.MarketPriceResponse;
import com.alpsahan.entity.AlertCondition;
import com.alpsahan.entity.PriceAlert;
import com.alpsahan.entity.User;
import com.alpsahan.event.AlertTriggeredEvent;
import com.alpsahan.kafka.producer.AlertEventProducer;
import com.alpsahan.repository.PriceAlertRepository;

@ExtendWith(MockitoExtension.class)
public class PriceAlertMonitorServiceTest {

    @Mock
    private PriceAlertRepository priceAlertRepository;

    @Mock
    private MarketService marketService;

    @Mock
    private AlertEventProducer alertEventProducer;

    @InjectMocks
    private PriceAlertMonitorService priceAlertMonitorService;

    
    @Test
    void shouldTriggerAboveAlertWhenCurrentPriceIsHigherThanTarget() {

        User user = new User();
        user.setId(1L);
        user.setEmail("alp@test.com");

        PriceAlert alert = new PriceAlert();
        alert.setId(10L);
        alert.setSymbol("BTCUSDT");
        alert.setTargetPrice(new BigDecimal("100000"));
        alert.setCondition(AlertCondition.ABOVE);
        alert.setActive(true);
        alert.setUser(user);

        when(priceAlertRepository.findByActiveTrue())
                .thenReturn(List.of(alert));

        when(marketService.getPrice("BTCUSDT"))
                .thenReturn(
                        new MarketPriceResponse(
                                "BTCUSDT",
                                new BigDecimal("110000")));

        
        priceAlertMonitorService.checkAlerts();

        assertFalse(alert.isActive());

        verify(priceAlertRepository)
                .save(alert);

        ArgumentCaptor<AlertTriggeredEvent> eventCaptor = ArgumentCaptor.forClass(
        		AlertTriggeredEvent.class);

        verify(alertEventProducer)
                .sendAlertTriggeredEvent(eventCaptor.capture());

        AlertTriggeredEvent event = eventCaptor.getValue();

        assertEquals(10L, event.getAlertId());
        assertEquals("alp@test.com", event.getEmail());
        assertEquals("BTCUSDT", event.getSymbol());
        assertEquals(
                new BigDecimal("100000"),
                event.getTargetPrice());
        
        assertEquals(
                new BigDecimal("110000"),
                event.getCurrentPrice());
        
        assertEquals(
                AlertCondition.ABOVE,
                event.getCondition());
    }
    
    
    
    @Test
    void shouldNotTriggerAlertWhenConditionIsNotMet() {

        User user = new User();
        user.setId(1L);
        user.setEmail("alp@test.com");

        PriceAlert alert = new PriceAlert();
        alert.setId(10L);
        alert.setSymbol("BTCUSDT");
        alert.setTargetPrice(new BigDecimal("100000"));
        alert.setCondition(AlertCondition.ABOVE);
        alert.setActive(true);
        alert.setUser(user);

        when(priceAlertRepository.findByActiveTrue())
                .thenReturn(List.of(alert));

        when(marketService.getPrice("BTCUSDT"))
                .thenReturn(
                        new MarketPriceResponse(
                                "BTCUSDT",
                                new BigDecimal("90000")));

        priceAlertMonitorService.checkAlerts();

        assertTrue(alert.isActive());

        verify(priceAlertRepository, never())
                .save(any(PriceAlert.class));

        verify(alertEventProducer, never())
                .sendAlertTriggeredEvent(
                        any(AlertTriggeredEvent.class));
    }
    
    
    
    @Test
    void shouldTriggerBelowAlertWhenCurrentPriceIsLowerThanTarget() {

        User user = new User();
        user.setId(1L);
        user.setEmail("alp@test.com");

        PriceAlert alert = new PriceAlert();
        alert.setId(20L);
        alert.setSymbol("BTCUSDT");
        alert.setTargetPrice(new BigDecimal("100000"));
        alert.setCondition(AlertCondition.BELOW);
        alert.setActive(true);
        alert.setUser(user);

        when(priceAlertRepository.findByActiveTrue())
                .thenReturn(List.of(alert));

        when(marketService.getPrice("BTCUSDT"))
                .thenReturn(
                        new MarketPriceResponse(
                                "BTCUSDT",
                                new BigDecimal("90000")));

        priceAlertMonitorService.checkAlerts();

        assertFalse(alert.isActive());

        verify(priceAlertRepository).save(alert);

        ArgumentCaptor<AlertTriggeredEvent> eventCaptor = ArgumentCaptor.forClass(
                AlertTriggeredEvent.class);

        verify(alertEventProducer)
                .sendAlertTriggeredEvent(eventCaptor.capture());

        AlertTriggeredEvent event = eventCaptor.getValue();

        assertEquals(20L, event.getAlertId());
        assertEquals("alp@test.com", event.getEmail());
        assertEquals("BTCUSDT", event.getSymbol());

        assertEquals(
                new BigDecimal("100000"),
                event.getTargetPrice());

        assertEquals(
                new BigDecimal("90000"),
                event.getCurrentPrice());

        assertEquals(
                AlertCondition.BELOW,
                event.getCondition());
    }
    
}