package com.parcelapp.order.exception;

import org.springframework.http.HttpStatus;

public class IllegalOrderStatusException extends BusinessException {
	public IllegalOrderStatusException(String message) {
		super(HttpStatus.CONFLICT, message);
	}
}
