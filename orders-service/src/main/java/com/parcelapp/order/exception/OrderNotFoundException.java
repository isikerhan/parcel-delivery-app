package com.parcelapp.order.exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends BusinessException {
	public OrderNotFoundException() {
		super(HttpStatus.NOT_FOUND, "Order not found!");
	}
}
