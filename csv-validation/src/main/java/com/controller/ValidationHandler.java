package com.controller;

import java.util.ArrayList;
import java.util.List;

import com.model.ValidateResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ValidationHandler extends ResponseEntityExceptionHandler{
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
																  HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		List<String> errors = new ArrayList<>();

		ex.getBindingResult().getAllErrors().forEach((error) ->{
			
			String fieldName = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			errors.add(message);
		});
		ValidateResponse validateResponse = new ValidateResponse(errors);
		return new ResponseEntity<Object>(validateResponse, HttpStatus.BAD_REQUEST);
	}

}
