package com.parcelapp.order.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
public class CreateDeliveryMovementModel {
	@NotNull(message = "location is mandatory")
	private LocationModel location;
	private String description;
	@NotNull(message = "movement_date is mandatory")
	private LocalDateTime movementDate;

	@Data
	public static class LocationModel {
		private String locationDescription;
		@NotNull(message = "latitude is mandatory")
		private Double latitude;
		@NotNull(message = "longitude is mandatory")
		private Double longitude;
		@NotNull(message = "district is mandatory")
		private String district;
		@NotNull(message = "city is mandatory")
		private String city;
		@NotBlank(message = "country_code is mandatory")
		@Pattern(regexp = "[A-Z]{2}",
				message = "country_code must consist of 2 uppercase letters")
		private String countryCode;
	}
}
