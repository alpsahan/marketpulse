package com.alpsahan.dto;

import java.math.BigDecimal;

import com.alpsahan.entity.AlertCondition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PriceAlertResponse {
	
	private Long id;
	private String symbol;
	private BigDecimal targetPrice;
	private AlertCondition condition;
	private boolean active;

}
