package com.alpsahan.specification;

import org.springframework.data.jpa.domain.Specification;

import com.alpsahan.entity.AlertCondition;
import com.alpsahan.entity.PriceAlert;

public class PriceAlertSpecification {
	
	public static Specification<PriceAlert> hasSymbol(String symbol) {
		
		return (root, query, criteriaBuilder ) -> 
			criteriaBuilder.equal(
					root.get("symbol"),
					symbol);
	}
	
	
	public static Specification<PriceAlert> hasActive(Boolean active) {
			
		return (root, query, criteriaBuilder ) -> 
			criteriaBuilder.equal(
					root.get("active"),
					active);
	}
	
	public static Specification<PriceAlert> hasCondition(AlertCondition condition) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("condition"),
                        condition);

}
	
	public static Specification<PriceAlert> belongsToUser(String email) {

	    return (root, query, criteriaBuilder) ->
	            criteriaBuilder.equal(
	                    root.get("user").get("email"),
	                    email);
	}

}