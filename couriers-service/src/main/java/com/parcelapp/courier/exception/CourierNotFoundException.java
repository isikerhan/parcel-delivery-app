package com.parcelapp.courier.exception;

import org.springframework.http.HttpStatus;

public class CourierNotFoundException extends BusinessException {
	public CourierNotFoundException() {
		super(HttpStatus.NOT_FOUND, "Courier not found!");
	}
}
