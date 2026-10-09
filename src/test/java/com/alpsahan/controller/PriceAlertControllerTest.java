package com.alpsahan.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.alpsahan.config.SecurityConfig;
import com.alpsahan.dto.CreatePriceAlertRequest;
import com.alpsahan.dto.PriceAlertResponse;
import com.alpsahan.entity.AlertCondition;
import com.alpsahan.exception.JwtAuthenticationEntryPoint;
import com.alpsahan.service.PriceAlertService;

@WebMvcTest(PriceAlertController.class)
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class})
public class PriceAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PriceAlertService priceAlertService;

    @MockitoBean
    private JwtDecoder jwtDecoder;
    
    

    @Test
    void shouldCreateAlertSuccessfully() throws Exception {

        PriceAlertResponse response = new PriceAlertResponse(
                        1L,
                        "BTCUSDT",
                        new BigDecimal("100000"),
                        AlertCondition.ABOVE,
                        true);

        when(priceAlertService.createAlert(
                any(CreatePriceAlertRequest.class),
                eq("alp@test.com")
        )).thenReturn(response);

        mockMvc.perform(
                post("/api/v1/alerts")

                        .with(jwt().jwt(jwt ->
                                jwt.subject("alp@test.com")
                        ))

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                                {
                                  "symbol": "BTCUSDT",
                                  "targetPrice": 100000,
                                  "condition": "ABOVE"
                                }
                                """))

        .andExpect(status().isCreated())

        .andExpect(jsonPath("$.id")
                .value(1))

        .andExpect(jsonPath("$.symbol")
                .value("BTCUSDT"))

        .andExpect(jsonPath("$.targetPrice")
                .value(100000))

        .andExpect(jsonPath("$.condition")
                .value("ABOVE"))

        .andExpect(jsonPath("$.active")
                .value(true));
    }
    
    
    
    @Test
    void shouldReturnBadRequestWhenTargetPriceIsNegative() throws Exception {

        mockMvc.perform(
                post("/api/v1/alerts")

                        .with(jwt().jwt(jwt ->
                                jwt.subject("alp@test.com")
                        ))

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                                {
                                  "symbol": "BTCUSDT",
                                  "targetPrice": -1000,
                                  "condition": "ABOVE"
                                }
                                """)
        )

        .andExpect(status().isBadRequest())

        .andExpect(jsonPath("$.status")
                .value(400))

        .andExpect(jsonPath("$.message")
                .value("Target price must be greater than zero"));

        verify(priceAlertService, never())
                .createAlert(
                        any(CreatePriceAlertRequest.class),
                        anyString());
    }
    
    
    
    @Test
    void shouldReturnUnauthorizedWhenJwtIsMissing() throws Exception {

        mockMvc.perform(
                post("/api/v1/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "symbol": "BTCUSDT",
                                  "targetPrice": 100000,
                                  "condition": "ABOVE"
                                }
                                """))
        
        .andExpect(status().isUnauthorized());

        verify(priceAlertService, never())
                .createAlert(
                        any(CreatePriceAlertRequest.class),
                        anyString());
    }
    
    
    
    @Test
    void shouldDeleteAlertSuccessfully() throws Exception {

        Long alertId = 17L;

        mockMvc.perform(
                delete("/api/v1/alerts/{id}", alertId)

                        .with(jwt().jwt(jwt ->
                                jwt.subject("alp@test.com")
                        )))
        
        .andExpect(status().isNoContent());

        verify(priceAlertService)
                .deleteAlert(
                        alertId,
                        "alp@test.com");
    }
    
}