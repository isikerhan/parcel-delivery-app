package com.parcelapp.order.model;

import lombok.Data;

@Data
public class CourierModel {
	private Long id;
	private String userId;
	private CourierType type;
	private CourierStatus status;

	public enum CourierType {
		WALKING_COURIER, MOTO_COURIER, CAR_COURIER, BIKE_COURIER
	}

	public enum CourierStatus {
		AVAILABLE, UNAVAILABLE
	}
}

