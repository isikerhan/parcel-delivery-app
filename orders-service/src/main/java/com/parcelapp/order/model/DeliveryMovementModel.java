package com.parcelapp.order.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliveryMovementModel {
	private LocationModel location;
	private String description;
	private LocalDateTime movementDate;

	@Data
	public static class LocationModel {
		private String locationDescription;
		private Double latitude;
		private Double longitude;
		private String district;
		private String city;
		private String countryCode;
	}
}
