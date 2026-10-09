package com.alpsahan.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T>{
	
	private List<T> content;
	
	private int page;
	
	private int size;
	
	private long totalElements;
	
	private int totalPages;
	
	private boolean first;
	
	private boolean last;
	
	
	public static <T> PageResponse<T> from(Page<T> page) {
		
		 return new PageResponse<>(
	                page.getContent(),
	                page.getNumber(),
	                page.getSize(),
	                page.getTotalElements(),
	                page.getTotalPages(),
	                page.isFirst(),
	                page.isLast());
	}
}
