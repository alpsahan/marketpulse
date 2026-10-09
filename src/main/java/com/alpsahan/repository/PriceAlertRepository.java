package com.alpsahan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.alpsahan.entity.PriceAlert;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long>, JpaSpecificationExecutor<PriceAlert> {
	
	List<PriceAlert> findByActiveTrue();
	
	Optional<PriceAlert> findByIdAndUserEmail(Long id, String email);
	

}
