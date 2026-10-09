package com.alpsahan.dto;

import java.math.BigDecimal;

import com.alpsahan.entity.AlertCondition;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePriceAlertRequest {
	
	private String symbol;
	
	@Positive(message = "Target price must be greater than zero")
	private BigDecimal targetPrice;
	
	private AlertCondition condition;
	
	private Boolean active;

}
