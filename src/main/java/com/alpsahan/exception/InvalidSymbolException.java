package com.alpsahan.exception;

public class InvalidSymbolException extends RuntimeException{
	
	public InvalidSymbolException(String symbol) {
		
		super("Symbol not found: " + symbol);
	}

}
