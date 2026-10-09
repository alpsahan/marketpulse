package com.alpsahan.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.alpsahan.event.AlertTriggeredEvent;
import com.alpsahan.service.EmailNotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationConsumer {
	
	private final EmailNotificationService emailNotificationService;
	
	
	@KafkaListener(
			topics = "alert-triggered",
			groupId = "marketpulse-notification-group"
			)
	
	
	public void consume(AlertTriggeredEvent event) {
		
		log.info(
				
				"NOTIFICATION RECEIVED - Symbol: {}, Current Price: {}, Target Price: {}",
				event.getSymbol(),
				event.getCurrentPrice(),
				event.getTargetPrice());
		
		emailNotificationService.sendAlertEmail(event);
	}
}
