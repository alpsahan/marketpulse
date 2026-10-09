package com.alpsahan.event;

import java.math.BigDecimal;

import com.alpsahan.entity.AlertCondition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlertTriggeredEvent {
	
	private Long alertId;
	private String email;
	private String symbol;
	private BigDecimal targetPrice;
	private BigDecimal currentPrice;
	private AlertCondition condition;

}
