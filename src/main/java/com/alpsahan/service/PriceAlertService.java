package com.alpsahan.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
import com.alpsahan.specification.PriceAlertSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PriceAlertService {
	
	private final PriceAlertRepository priceAlertRepository;
	private final MarketService marketService;
	private final UserRepository userRepository;
	
	
	public PriceAlertResponse createAlert(CreatePriceAlertRequest request, String email) {
		
		User user = userRepository
				.findByEmail(email)
				.orElseThrow(UserNotFoundException::new);
		
		String symbol = request.getSymbol().toUpperCase();
		
		marketService.getPrice(symbol);
		
		
		PriceAlert alert = new PriceAlert();
		
		alert.setSymbol(symbol);
		alert.setTargetPrice(request.getTargetPrice());
		alert.setCondition(request.getCondition());
		alert.setActive(true);
		
		alert.setUser(user);
		
		
		PriceAlert savedAlert = priceAlertRepository.save(alert);
		
		return new PriceAlertResponse(
                savedAlert.getId(),
                savedAlert.getSymbol(),
                savedAlert.getTargetPrice(),
                savedAlert.getCondition(),
                savedAlert.isActive());
}
	
	
	public Page<PriceAlertResponse> getAlerts(
			String email,
			String symbol,
			Boolean active,
			AlertCondition condition,
			Pageable pageable) {
		
		Specification<PriceAlert> spec = PriceAlertSpecification.belongsToUser(email);
		
		if(symbol != null) {
			
			spec = spec.and(PriceAlertSpecification.hasSymbol(symbol.toUpperCase()));
			
		}
		
		if(active != null) {
			
			spec = spec.and(PriceAlertSpecification.hasActive(active));
			
		}
		
		if(condition != null) {
			
			spec = spec.and(PriceAlertSpecification.hasCondition(condition));
		}
		
		Page<PriceAlert> alerts = priceAlertRepository.findAll(spec, pageable);
		
		return alerts.map(alert -> new PriceAlertResponse(
						alert.getId(),
						alert.getSymbol(),
						alert.getTargetPrice(),
						alert.getCondition(),
						alert.isActive()));
	}
	
	
	public void deleteAlert(Long alertId, String email) {
		
		PriceAlert alert = priceAlertRepository.findByIdAndUserEmail(alertId, email)
				.orElseThrow(PriceAlertNotFoundException::new);
		
		priceAlertRepository.delete(alert);
	}
	
	
	public PriceAlertResponse updateAlert(Long alertId, String email, UpdatePriceAlertRequest request) {
		
		PriceAlert alert = priceAlertRepository.findByIdAndUserEmail(alertId, email)
				.orElseThrow(PriceAlertNotFoundException::new);
		
		if(request.getSymbol() !=null) {
			
			String symbol = request.getSymbol().toUpperCase();
			
			marketService.getPrice(symbol);
			
			alert.setSymbol(symbol);
			
		}
		
		if(request.getTargetPrice() != null) {
			alert.setTargetPrice(request.getTargetPrice());
		}
		
		if(request.getCondition() != null) {
			alert.setCondition(request.getCondition());
		}
		
		if(request.getActive() != null) {
			alert.setActive(request.getActive());
		}
		
		
		PriceAlert updatedAlert = priceAlertRepository.save(alert);
		
		return new PriceAlertResponse(
	            updatedAlert.getId(),
	            updatedAlert.getSymbol(),
	            updatedAlert.getTargetPrice(),
	            updatedAlert.getCondition(),
	            updatedAlert.isActive());
		
	}
	
	
	
	
}