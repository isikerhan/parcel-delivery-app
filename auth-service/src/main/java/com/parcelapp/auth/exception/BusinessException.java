package com.parcelapp.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends Exception {

	private final HttpStatus status;

	public BusinessException(HttpStatus status) {
		super();
		this.status = status;
	}

	public BusinessException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}
}
