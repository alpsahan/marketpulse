package com.alpsahan.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.alpsahan.event.AlertTriggeredEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AlertEventProducer {
	
	private final KafkaTemplate<String, AlertTriggeredEvent> kafkaTemplate;
	
	public void sendAlertTriggeredEvent(AlertTriggeredEvent event) {
		
		kafkaTemplate.send("alert-triggered", event);
	}

}
