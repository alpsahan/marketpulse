package com.alpsahan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.alpsahan.dto.CreatePriceAlertRequest;
import com.alpsahan.dto.PriceAlertResponse;
import com.alpsahan.dto.UpdatePriceAlertRequest;
import com.alpsahan.entity.AlertCondition;
import com.alpsahan.entity.PriceAlert;
import com.alpsahan.entity.User;
import com.alpsahan.exception.PriceAlertNotFoundException;
import com.alpsahan.exception.UserNotFoundException;
import com.alpsahan.repository.PriceAlertRepository;
import com.alpsahan.repository.UserRepository;


@ExtendWith(MockitoExtension.class)
public class PriceAlertServiceTest {
	
	@Mock
	private PriceAlertRepository priceAlertRepository;
	
	@Mock
	private MarketService marketService;
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private PriceAlertService priceAlertService;
	
	
	@Test
	void shouldDeleteAlertWhenAlertBelongsToUser() {

        Long alertId = 1L;
        String email = "alp@test.com";

        PriceAlert alert = new PriceAlert();
        alert.setId(alertId);

        when(priceAlertRepository
                .findByIdAndUserEmail(alertId, email))
                .thenReturn(Optional.of(alert));

        priceAlertService.deleteAlert(alertId, email);

        verify(priceAlertRepository).delete(alert);
    }
	
	
	@Test
	void shouldThrowExceptionWhenAlertNotFound() {

	    Long alertId = 99L;
	    String email = "alp@test.com";

	    when(priceAlertRepository
	            .findByIdAndUserEmail(alertId, email))
	            .thenReturn(Optional.empty());

	    PriceAlertNotFoundException exception =
	            assertThrows(
	                    PriceAlertNotFoundException.class,
	                    () -> priceAlertService.deleteAlert(alertId, email));

	    assertEquals(
	            "Price alert not found",
	            exception.getMessage());
	}
	
	
	@Test
	void shouldCreateAlertSuccessfully() {

	    String email = "alp@test.com";

	    CreatePriceAlertRequest request = new CreatePriceAlertRequest();
	    request.setSymbol("btcusdt");
	    request.setTargetPrice(new BigDecimal("100000"));
	    request.setCondition(AlertCondition.ABOVE);

	    User user = new User();
	    user.setId(1L);
	    user.setEmail(email);

	    PriceAlert savedAlert = new PriceAlert();
	    savedAlert.setId(10L);
	    savedAlert.setSymbol("BTCUSDT");
	    savedAlert.setTargetPrice(new BigDecimal("100000"));
	    savedAlert.setCondition(AlertCondition.ABOVE);
	    savedAlert.setActive(true);
	    savedAlert.setUser(user);

	    when(userRepository.findByEmail(email))
	            .thenReturn(Optional.of(user));

	    when(priceAlertRepository.save(any(PriceAlert.class)))
	            .thenReturn(savedAlert);

	    PriceAlertResponse response = priceAlertService.createAlert(request, email);

	    ArgumentCaptor<PriceAlert> alertCaptor = ArgumentCaptor.forClass(PriceAlert.class);

	    verify(priceAlertRepository).save(alertCaptor.capture());

	    PriceAlert capturedAlert = alertCaptor.getValue();

	    assertEquals("BTCUSDT", capturedAlert.getSymbol());
	    assertEquals(new BigDecimal("100000"), capturedAlert.getTargetPrice());
	    
	    assertEquals(AlertCondition.ABOVE, capturedAlert.getCondition());
	    
	    assertTrue(capturedAlert.isActive());
	    assertEquals(user, capturedAlert.getUser());
	    
	    assertEquals(10L, response.getId());
	    assertEquals("BTCUSDT", response.getSymbol());
	    assertEquals(new BigDecimal("100000"), response.getTargetPrice());
	    assertEquals(AlertCondition.ABOVE, response.getCondition());
	    
	    assertTrue(response.isActive());

	    verify(userRepository).findByEmail(email);
	    verify(marketService).getPrice("BTCUSDT");
	}
	
	
	@Test
	void shouldThrowExceptionWhenUserNotFoundWhileCreatingAlert() {

	    String email = "notfound@test.com";

	    CreatePriceAlertRequest request = new CreatePriceAlertRequest();
	    request.setSymbol("BTCUSDT");
	    request.setTargetPrice(new BigDecimal("100000"));
	    request.setCondition(AlertCondition.ABOVE);

	    when(userRepository.findByEmail(email))
	            .thenReturn(Optional.empty());

	    assertThrows(UserNotFoundException.class,
	            () -> priceAlertService.createAlert(request, email));

	    verify(priceAlertRepository, never())
	            .save(any(PriceAlert.class));

	    verify(marketService, never())
	            .getPrice(anyString());
	}
	
	
	@Test
	void shouldUpdateOnlyTargetPrice() {

	    Long alertId = 1L;
	    String email = "alp@test.com";

	    User user = new User();
	    user.setId(1L);
	    user.setEmail(email);

	    PriceAlert alert = new PriceAlert();
	    alert.setId(alertId);
	    alert.setSymbol("BTCUSDT");
	    alert.setTargetPrice(new BigDecimal("100000"));
	    alert.setCondition(AlertCondition.ABOVE);
	    alert.setActive(true);
	    alert.setUser(user);

	    UpdatePriceAlertRequest request = new UpdatePriceAlertRequest();

	    request.setTargetPrice(new BigDecimal("120000"));

	    when(priceAlertRepository
	            .findByIdAndUserEmail(alertId, email))
	            .thenReturn(Optional.of(alert));

	    when(priceAlertRepository.save(alert))
	            .thenReturn(alert);

	    PriceAlertResponse response =
	            priceAlertService.updateAlert(
	                    alertId,
	                    email,
	                    request);

	    assertEquals(
	            new BigDecimal("120000"),
	            response.getTargetPrice());

	    assertEquals("BTCUSDT", response.getSymbol());
	    assertEquals(AlertCondition.ABOVE, response.getCondition());
	    assertTrue(response.isActive());

	    verify(priceAlertRepository).save(alert);

	    verify(marketService, never())
	            .getPrice(anyString());
	}
	
	
	@Test
	void shouldUpdateSymbolAndValidateMarketPrice() {

	    Long alertId = 1L;
	    String email = "alp@test.com";

	    User user = new User();
	    user.setId(1L);
	    user.setEmail(email);

	    PriceAlert alert = new PriceAlert();
	    alert.setId(alertId);
	    alert.setSymbol("BTCUSDT");
	    alert.setTargetPrice(new BigDecimal("100000"));
	    alert.setCondition(AlertCondition.ABOVE);
	    alert.setActive(true);
	    alert.setUser(user);

	    UpdatePriceAlertRequest request = new UpdatePriceAlertRequest();

	    request.setSymbol("ethusdt");

	    when(priceAlertRepository
	            .findByIdAndUserEmail(alertId, email))
	            .thenReturn(Optional.of(alert));

	    when(priceAlertRepository.save(alert))
	            .thenReturn(alert);

	    PriceAlertResponse response =
	            priceAlertService.updateAlert(
	                    alertId,
	                    email,
	                    request);

	    assertEquals("ETHUSDT", response.getSymbol());

	    assertEquals(
	            new BigDecimal("100000"),
	            response.getTargetPrice());

	    assertEquals(
	            AlertCondition.ABOVE,
	            response.getCondition());

	    assertTrue(response.isActive());

	    verify(marketService).getPrice("ETHUSDT");

	    verify(priceAlertRepository).save(alert);
	}
	
	
	
	@Test
	void shouldThrowExceptionWhenAlertNotFoundWhileUpdating() {

	    Long alertId = 99L;
	    String email = "alp@test.com";

	    UpdatePriceAlertRequest request = new UpdatePriceAlertRequest();

	    request.setSymbol("ethusdt");
	    request.setTargetPrice(new BigDecimal("120000"));

	    when(priceAlertRepository
	            .findByIdAndUserEmail(alertId, email))
	            .thenReturn(Optional.empty());

	    assertThrows(PriceAlertNotFoundException.class,
	            () -> priceAlertService.updateAlert(
	                    alertId,
	                    email,
	                    request));

	    verify(marketService, never())
	            .getPrice(anyString());

	    verify(priceAlertRepository, never())
	            .save(any(PriceAlert.class));
	}
	
	
	@Test
	void shouldReturnAlertsWithPagination() {

	    String email = "alp@test.com";

	    Pageable pageable = PageRequest.of(
	            0,
	            2,
	            Sort.by("id").descending());

	    PriceAlert alert1 = new PriceAlert();
	    alert1.setId(10L);
	    alert1.setSymbol("BTCUSDT");
	    alert1.setTargetPrice(new BigDecimal("100000"));
	    alert1.setCondition(AlertCondition.ABOVE);
	    alert1.setActive(true);

	    PriceAlert alert2 = new PriceAlert();
	    alert2.setId(9L);
	    alert2.setSymbol("ETHUSDT");
	    alert2.setTargetPrice(new BigDecimal("5000"));
	    alert2.setCondition(AlertCondition.ABOVE);
	    alert2.setActive(false);

	    Page<PriceAlert> alertPage = new PageImpl<>(
	                    List.of(alert1, alert2),
	                    pageable,
	                    5);

	    when(priceAlertRepository.findAll(
	            ArgumentMatchers.<Specification<PriceAlert>>any(),
	            eq(pageable)
	    )).thenReturn(alertPage);

	    Page<PriceAlertResponse> response = priceAlertService.getAlerts(
	                    email,
	                    null,
	                    null,
	                    null,
	                    pageable);

	    assertEquals(2, response.getContent().size());

	    assertEquals(5, response.getTotalElements());
	    assertEquals(3, response.getTotalPages());
	    assertEquals(0, response.getNumber());
	    assertEquals(2, response.getSize());

	    assertEquals(
	            10L,
	            response.getContent().get(0).getId());

	    assertEquals(
	            "BTCUSDT",
	            response.getContent().get(0).getSymbol());

	    assertEquals(
	            9L,
	            response.getContent().get(1).getId());

	    verify(priceAlertRepository).findAll(
	            ArgumentMatchers.<Specification<PriceAlert>>any(),
	            eq(pageable));
	}
	
	

	
}
