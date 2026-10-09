package com.alpsahan.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alpsahan.dto.MarketPriceResponse;
import com.alpsahan.entity.AlertCondition;
import com.alpsahan.entity.PriceAlert;
import com.alpsahan.event.AlertTriggeredEvent;
import com.alpsahan.kafka.producer.AlertEventProducer;
import com.alpsahan.repository.PriceAlertRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PriceAlertMonitorService {
	
	private final PriceAlertRepository priceAlertRepository;
	private final MarketService marketService;
	private final AlertEventProducer alertEventProducer;
	
	@Scheduled(fixedRate = 10000)
	public void checkAlerts() {
		
		List<PriceAlert> activeAlerts = priceAlertRepository.findByActiveTrue();
		
		for (PriceAlert alert : activeAlerts) {
			
			MarketPriceResponse marketPrice = marketService.getPrice(alert.getSymbol());
			
			BigDecimal currentPrice = marketPrice.getPrice();
			
			if (isTriggered(alert, currentPrice)) {
				
				log.info(
                        "ALERT TRIGGERED - Symbol: {}, Current Price: {}, Target Price: {}",
                        alert.getSymbol(),
                        currentPrice,
                        alert.getTargetPrice());
				
				alert.setActive(false);
				
				priceAlertRepository.save(alert);
				
				AlertTriggeredEvent event = new AlertTriggeredEvent(
						alert.getId(),
						alert.getUser().getEmail(),
						alert.getSymbol(),
						alert.getTargetPrice(),
						currentPrice,
						alert.getCondition());
				
				alertEventProducer.sendAlertTriggeredEvent(event);			
			}
		}
	}
					
	
	private boolean isTriggered(PriceAlert alert, BigDecimal currentPrice) {
		
		int comparison = currentPrice.compareTo(alert.getTargetPrice());
		
		if(alert.getCondition() == AlertCondition.ABOVE) {
			
			return comparison > 0;
		}
		
		if (alert.getCondition() == AlertCondition.BELOW) {
		
			return comparison < 0 ;
		}
		
		return false;
	}
}
