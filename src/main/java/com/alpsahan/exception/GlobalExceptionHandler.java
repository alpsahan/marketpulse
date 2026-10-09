package com.alpsahan.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alpsahan.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	
	 	@ExceptionHandler(InvalidSymbolException.class)
	    public ResponseEntity<ErrorResponse> handleInvalidSymbolException(InvalidSymbolException exception) {

	        ErrorResponse response = new ErrorResponse(
	                HttpStatus.NOT_FOUND.value(),
	                exception.getMessage());

	        return ResponseEntity
	                .status(HttpStatus.NOT_FOUND)
	                .body(response);
	    }
	 	
	 	@ExceptionHandler(MethodArgumentNotValidException.class)
	 	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
	 		
	 		String message = exception
	 				.getBindingResult()
	 				.getFieldErrors()
	 				.get(0)
	 				.getDefaultMessage();
	 		
	 		ErrorResponse response = new ErrorResponse(
	 				HttpStatus.BAD_REQUEST.value(),
	 				message);
	 		
	 		return ResponseEntity
	 				.status(HttpStatus.BAD_REQUEST)
	 				.body(response);
	 	}
	 	
	 	
	 	@ExceptionHandler(UserNotFoundException.class) 
	 	public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception) {
	 		
	 		ErrorResponse response = new ErrorResponse(
	 				HttpStatus.NOT_FOUND.value(),
	 				exception.getMessage());
	 		
	 		return ResponseEntity
	 				.status(HttpStatus.NOT_FOUND)
	 				.body(response);
	 		
	 	}
	 	
	 	@ExceptionHandler(InvalidCredentialsException.class)
	 	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException exception) {
	 		
	 		ErrorResponse response = new ErrorResponse(
	 				HttpStatus.UNAUTHORIZED.value(),
	 				exception.getMessage());
	 		
	 		return ResponseEntity
	 				.status(HttpStatus.UNAUTHORIZED)
	 				.body(response);
	 		
	 	}
	 	
	 	@ExceptionHandler(EmailAlreadyExistsException.class)
	 	public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException exception) {
	 		
	 		ErrorResponse response = new ErrorResponse(
	 				HttpStatus.CONFLICT.value(),
	 				exception.getMessage());
	 		
	 		return ResponseEntity
	 				.status(HttpStatus.CONFLICT)
	 				.body(response);
	 	}
	 	
	 	
	 	@ExceptionHandler(PriceAlertNotFoundException.class)
	 	public ResponseEntity<ErrorResponse> handlePriceAlertNotFoundExcepiton(PriceAlertNotFoundException exception) {
	 		
	 		ErrorResponse response = new ErrorResponse(
	 				HttpStatus.NOT_FOUND.value(),
	 				exception.getMessage()); 
	 		
	 		return ResponseEntity
	 				.status(HttpStatus.NOT_FOUND)
	 				.body(response);
	 	}
	 	
	 	
	 	
}
