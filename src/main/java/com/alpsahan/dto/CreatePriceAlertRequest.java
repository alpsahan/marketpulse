package com.alpsahan.dto;

import java.math.BigDecimal;

import com.alpsahan.entity.AlertCondition;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePriceAlertRequest {
	
	@NotBlank(message = "Symbol cannot be empty")
	private String symbol;
	
	@NotNull(message = "Target price cannot be null")
	@Positive(message = "Target price must be greater than zero")
	private BigDecimal targetPrice;
	
	@NotNull(message = "Condition cannot be null")
	private AlertCondition condition;
	
}
