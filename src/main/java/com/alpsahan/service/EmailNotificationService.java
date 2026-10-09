package com.alpsahan.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.alpsahan.event.AlertTriggeredEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmailNotificationService {
	
	private final JavaMailSender mailSender;
	
	public void sendAlertEmail(AlertTriggeredEvent event) {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setTo(event.getEmail());
		message.setSubject("MarketPulse - Price Alert Triggered");
		
		message.setText(
				
				 "Your price alert has been triggered!\n\n" +
			     "Symbol: " + event.getSymbol() + "\n" +
			     "Current Price: " + event.getCurrentPrice() + "\n" +
			     "Target Price: " + event.getTargetPrice() + "\n" +
			     "Condition: " + event.getCondition());
		
		mailSender.send(message);
		
	}

}
